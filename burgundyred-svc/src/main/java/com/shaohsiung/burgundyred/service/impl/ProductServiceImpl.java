package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.converter.ObjectBytesConverter;
import com.shaohsiung.burgundyred.document.ProductDocument;
import com.shaohsiung.burgundyred.dto.ProductItemDto;
import com.shaohsiung.burgundyred.dto.ProductStockDto;
import com.shaohsiung.burgundyred.enums.ProductState;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.mapper.ProductMapper;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service(version = "1.0.0")
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 根据类目id获取产品列表
     * <p>
     * 前台
     *
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<Product> getProductListByCategoryId(String categoryId, int pageNum, int pageSize) {
        int offset = pageNum * pageSize;
        List<Product> result = productMapper.getProductListByCategoryId(categoryId, new RowBounds(offset, pageSize));
        log.info("【产品模块】根据产品类目获取产品列表：{}", result);
        return result;
    }

    /**
     * 根据产品id获取产品详情
     * <p>
     * 前台
     *
     * @param productId
     * @return
     */
    @Override
    public Product getProductById(String productId) {
        Product result = productMapper.getProductById(productId);
        log.info("【产品模块】获取产品：{}", result);
        return result;
    }

    /**
     * 添加商品
     * <p>
     * 后台
     *
     * @param product
     * @return
     */
    @Override
    @Transactional
    public Product addProduct(Product product) {

        product.setCreateTime(new Date());
        product.setId(idWorker.nextId()+"");
        // 商品状态默认上架
        product.setState(ProductState.ON_THE_SHELF);
        product.setUpdateTime(new Date());

        int save = productMapper.save(product);
        if (save == 1) {
            log.info("添加产品：{}", product);

            // 同步索引库
            try {
                ProductDocument productDocument = new ProductDocument();
                BeanUtils.copyProperties(product, productDocument);

                byte[] bytes = ObjectBytesConverter.getBytesFromObject(productDocument);
                rabbitTemplate.convertAndSend("exchange", "topic.messages", bytes);
            } catch (Exception e) {
                log.warn("【产品模块】添加产品，发送消息失败");
                throw new BackEndException(ErrorState.PRODUCT_CREATE_FAILED);
            }

            return product;
        }
        throw new BackEndException(ErrorState.PRODUCT_CREATE_FAILED);
    }

    /**
     * 获取商品库存
     *
     * @param productId
     * @return
     */
    @Override
    public Integer getStockByProductId(String productId) {
        Integer stock = productMapper.getStockByProductId(productId);
        if (stock == null) {
            throw new FrontEndException(ErrorState.ACQUIRE_PRODUCT_STOCK_FAILED);
        }
        return stock;
    }

    /**
     * 增加商品库存
     *
     * @param productStockDtoList
     * @return
     */
    @Override
    @Transactional
    public BaseResponse increaseStock(List<ProductStockDto> productStockDtoList) {
        productStockDtoList.forEach(productStockDto -> {
            Product product = productMapper.getProductById(productStockDto.getProductId());
            if (product == null) {
                throw new FrontEndException(ErrorState.PRODUCT_NOT_EXIST);
            }
            product.setStock(product.getStock() + productStockDto.getQuanity());

            int update = productMapper.updateStock(product);
            if (update == 1) {
                log.info("【商品基础SVC】商品库存更新成功，商品id：{}，增加库存：{}", productStockDto.getProductId(), productStockDto.getQuanity());
            } else {
                log.warn("【商品基础SVC】商品库存更新失败，商品id：{}，增加库存：{}", productStockDto.getProductId(), productStockDto.getQuanity());
                throw new FrontEndException(ErrorState.PRODUCT_STOCK_UPDATE_FAILED);
            }
        });
        return BaseResponseUtils.success();
    }

    /**
     * 减少商品库存
     *
     * @param productStockDtoList
     * @return
     */
    @Override
    @Transactional
    public BaseResponse decreaseStock(List<ProductStockDto> productStockDtoList) {
        productStockDtoList.forEach(productStockDto -> {
            Product product = productMapper.getProductById(productStockDto.getProductId());
            if (product == null) {
                throw new FrontEndException(ErrorState.PRODUCT_NOT_EXIST);
            }

            Integer result = product.getStock() - productStockDto.getQuanity();
            if (result < 0) {
                log.warn("【商品基础SVC】商品库存不足，商品id：{}，减少库存：{}，实际商品库存：{}", productStockDto.getProductId(),
                        productStockDto.getQuanity(),
                        product.getStock());
                String message = String.format("商品：%s 库存不足！该商品实际库存：%s", product.getName(), product.getStock());
                throw new FrontEndException(ErrorState.PRODUCT_STOCK_LACK, message);
            }
            product.setStock(result);

            int update = productMapper.updateStock(product);
            if (update == 1) {
                log.info("【商品基础SVC】商品库存更新成功，商品id：{}，减少库存：{}", productStockDto.getProductId(), productStockDto.getQuanity());
            } else {
                log.warn("【商品基础SVC】商品库存更新失败，商品id：{}，减少库存：{}", productStockDto.getProductId(), productStockDto.getQuanity());
                throw new FrontEndException(ErrorState.PRODUCT_STOCK_UPDATE_FAILED);
            }
        });
        return BaseResponseUtils.success();
    }

    @Override
    public List<ProductItemDto> latestStyle(int limit) {
        List<Product> products = productMapper.latestStyle(limit);
        List<ProductItemDto> result = products.stream().map(product -> {
            ProductItemDto productItemDto = new ProductItemDto();
            BeanUtils.copyProperties(product, productItemDto);
            return productItemDto;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<ProductItemDto> recommendedStyle(int limit) {
        List<Product> products = productMapper.recommendedStyle(limit);
        List<ProductItemDto> result = products.stream().map(product -> {
            ProductItemDto productItemDto = new ProductItemDto();
            BeanUtils.copyProperties(product, productItemDto);
            return productItemDto;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * 亲民款式
     * @param limit
     * @return
     */
    @Override
    public List<ProductItemDto> intimateStyle(int limit) {
        List<Product> products = productMapper.intimateStyle(limit);
        List<ProductItemDto> result = products.stream().map(product -> {
            ProductItemDto productItemDto = new ProductItemDto();
            BeanUtils.copyProperties(product, productItemDto);
            return productItemDto;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<ProductItemDto> scarceStyle(int limit) {
        List<Product> products = productMapper.scarceStyle(limit);
        List<ProductItemDto> result = products.stream().map(product -> {
            ProductItemDto productItemDto = new ProductItemDto();
            BeanUtils.copyProperties(product, productItemDto);
            return productItemDto;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<ProductItemDto> clearanceStyle(int limit) {
        List<Product> products = productMapper.clearanceStyle(limit);
        List<ProductItemDto> result = products.stream().map(product -> {
            ProductItemDto productItemDto = new ProductItemDto();
            BeanUtils.copyProperties(product, productItemDto);
            return productItemDto;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * 卖家商品列表
     *
     * @return
     */
    @Override
    public List<Product> sellerProductList(int pageNum, int pageSize) {
        int offset = pageNum * pageSize;
        return productMapper.sellerProductList(new RowBounds(offset, pageSize));
    }

    /**
     * 卖家商品列表总数量
     *
     * @return
     */
    @Override
    public Integer sellerProductListTotalRecord() {
        return productMapper.sellerProductListTotalRecord();
    }

    /**
     * 上架商品
     *
     * @param productId
     * @return
     */
    @Override
    public BaseResponse onShelves(String productId) {
        int update = productMapper.onShelves(productId);
        if (update == 1) {
            log.info("【商品基础SVC】商品设置为上架状态，商品id：{}", productId);
            return BaseResponseUtils.success();
        }
        throw new BackEndException(ErrorState.SET_PRODUCT_ON_THE_SHELF_FAILED);
    }

    /**
     * 下架商品
     *
     * @param productId
     * @return
     */
    @Override
    public BaseResponse remove(String productId) {
        int update = productMapper.remove(productId);
        if (update == 1) {
            log.info("【商品基础SVC】商品设置为下架状态，商品id：{}", productId);
            return BaseResponseUtils.success();
        }
        throw new BackEndException(ErrorState.PRODUCT_REMOVE_FAILED);
    }

    /**
     * 根据类目id获取产品数量
     *
     * @param categoryId
     * @return
     */
    @Override
    public Integer productTotalRecordByCategoryId(String categoryId) {
        return productMapper.productTotalRecordByCategoryId(categoryId);
    }
}
