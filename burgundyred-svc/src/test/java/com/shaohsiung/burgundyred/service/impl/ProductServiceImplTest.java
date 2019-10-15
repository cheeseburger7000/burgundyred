package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.dto.ProductStockDto;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.service.ProductService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductServiceImplTest {
    @Reference(version = "1.0.0")
    private ProductService productService;

    @Test
    public void getProductListByCategoryId() {
        List<Product> productList = productService.getProductListByCategoryId("0", 0, 2);
        Assert.assertNotEquals(0, productList.size());
    }

    @Test
    public void getProductById() {
        Product product = productService.getProductById("0");
        Assert.assertNotNull(product);
    }

    @Test
//    @Transactional
    public void addProduct() {
        // 请求参数参考
        Product product1 = Product.builder().name("智利 白酒  波塔酒莊 波塔白蘇維濃白葡萄酒")
                .detail("智利 紅酒  波塔酒莊 波塔卡本內蘇維濃紅酒产品描述")
                .categoryId("0")
                .stock(20)
                .price(new BigDecimal(351.00))
                .mainPicture("/images/智利 白酒  波塔酒莊 波塔白蘇維濃白葡萄酒-351.jpg")
                .subPicture("/images/智利 白酒  波塔酒莊 波塔白蘇維濃白葡萄酒-351-sub.jpg")
                .build();

        Product product2 = Product.builder().name("智利 紅酒  波塔酒莊 波塔卡本內蘇維濃紅酒")
                .detail("智利 紅酒  波塔酒莊 波塔卡本內蘇維濃紅酒产品描述")
                .categoryId("0")
                .stock(80)
                .price(new BigDecimal(390.00))
                .mainPicture("/images/智利 紅酒  波塔酒莊 波塔卡本內蘇維濃紅酒-390.jpg")
                .subPicture("/images/智利 紅酒  波塔酒莊 波塔卡本內蘇維濃紅酒-390-sub.jpg")
                .build();

        Product product3 = Product.builder().name("天山 遊花 純米吟釀生貯 300ml（熱銷完售)")
                .detail("天山 遊花 純米吟釀生貯 300ml（熱銷完售)产品描述")
                .categoryId("0")
                .stock(100)
                .price(new BigDecimal(600.00))
                .mainPicture("/images/天山 遊花 純米吟釀生貯 300ml（熱銷完售)-600.jpg")
                .subPicture("/images/天山 遊花 純米吟釀生貯 300ml（熱銷完售)-600-sub.jpg")
                .build();

        Product product4 = Product.builder().name("智利 紅酒  舒伯堡喜若紅酒 (完售補貨中)")
                .detail("智利 紅酒  舒伯堡喜若紅酒 (完售補貨中)产品描述")
                .categoryId("0")
                .stock(990)
                .price(new BigDecimal(360.00))
                .mainPicture("/images/智利 紅酒  舒伯堡喜若紅酒 (完售補貨中)-360.jpg")
                .subPicture("/images/智利 紅酒  舒伯堡喜若紅酒 (完售補貨中)-360-sub.jpg")
                .build();

        productService.addProduct(product1);
        productService.addProduct(product2);
        productService.addProduct(product3);
        productService.addProduct(product4);
    }

    @Test
    public void increaseStock() {
        ProductStockDto p1 = new ProductStockDto("1182854430526148608", 3);
        ProductStockDto p2 = new ProductStockDto("1182854432250007552", 4);
        List<ProductStockDto> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        BaseResponse baseResponse = productService.increaseStock(list);
    }

    @Test
    public void decreaseStock() {
        ProductStockDto p1 = new ProductStockDto("1182854430526148608", 3);
        ProductStockDto p2 = new ProductStockDto("1182854432250007552", 4);
        List<ProductStockDto> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        BaseResponse baseResponse = productService.decreaseStock(list);
    }

    @Test(expected = FrontEndException.class)
    public void decreaseStockEx() {
        ProductStockDto p1 = new ProductStockDto("1182854430526148608", 300);
        ProductStockDto p2 = new ProductStockDto("1182854432250007552", 4);
        List<ProductStockDto> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        BaseResponse baseResponse = productService.decreaseStock(list);
    }
}
