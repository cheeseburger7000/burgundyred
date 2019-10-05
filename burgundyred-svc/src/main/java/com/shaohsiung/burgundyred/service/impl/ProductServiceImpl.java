package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.converter.ObjectBytesConverter;
import com.shaohsiung.burgundyred.document.ProductDocument;
import com.shaohsiung.burgundyred.enums.ProductState;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.mapper.ProductMapper;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service(version = "1.0.0")
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
                throw new BackEndException("产品创建失败");
            }

            return product;
        }
        throw new BackEndException("产品创建失败");
    }

    /**
     *  TODO 减少产品库存
     * <p>
     * 前台订单服务
     *
     * @param productId
     * @param quanity
     * @return
     */
    @Override
    public Product incStock(String productId, int quanity) {
        return null;
    }
}
