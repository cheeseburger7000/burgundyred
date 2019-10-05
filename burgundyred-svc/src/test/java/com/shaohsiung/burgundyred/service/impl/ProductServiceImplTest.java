package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.service.ProductService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @Transactional
    public void addProduct() {
        // 请求参数参考
        Product product = Product.builder().name("招牌勃艮第红酒")
                .detail("招牌勃艮第红酒描述")
                .categoryId("0")
                .stock(90)
                .price(new BigDecimal(312.90))
                .mainPicture("http://xxx.jpg")
                .subPicture("http://xxx.jpg")
                .build();

        Product result = productService.addProduct(product);
        Assert.assertNotNull(result);
    }

    @Test
    public void incStock() {
    }
}
