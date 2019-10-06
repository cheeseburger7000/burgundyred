package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.model.Shipping;
import com.shaohsiung.burgundyred.service.ShippingService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ShippingServiceImplTest {

    @Reference(version = "1.0.0")
    private ShippingService shippingService;

    @Test
    public void addShipping() {
        Shipping shipping = Shipping.builder().userId("0")
                .receiverName("shaohsiung")
                .receiverMobile("13055228247")
                .receiverProvince("福建省")
                .receiverCity("厦门市")
                .receiverDistrict("集美区")
                .receiverAddress("诚毅学院")
                .receiverZip("3306")
                .build();

        Shipping save = shippingService.addShipping(shipping);
        Assert.assertNotNull(save);
    }

    @Test
    public void shippingList() {
        List<Shipping> shippings = shippingService.shippingList("0");
        Assert.assertNotEquals(0, shippings);
    }

    @Test
    public void updateShipping() {
        Shipping shipping = Shipping.builder().userId("0")
                .id("1180692054221656064")
                .receiverProvince("福建省1")
                .receiverCity("厦门市1")
                .receiverDistrict("集美区1")
                .build();
        Shipping update = shippingService.updateShipping(shipping);
        Assert.assertNotNull(update);
    }

    @Test
    @Transactional
    public void deleteShipping() {
        int delete = shippingService.deleteShipping("1180692054221656064", "0");
        Assert.assertEquals(1, delete);
    }
}
