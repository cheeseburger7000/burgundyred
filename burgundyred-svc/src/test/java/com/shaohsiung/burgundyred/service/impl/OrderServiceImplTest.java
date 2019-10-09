package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Order;
import com.shaohsiung.burgundyred.service.OrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderServiceImplTest {

    @Reference(version = "1.0.0")
    private OrderService orderService;

//    @Test(expected = FrontEndException.class)
//    public void createEx() {
//        String errorUserId = "0";
//        Order order = orderService.create(errorUserId, "1180692054221656064");
//    }

    @Test
    public void create() {
        Order order = orderService.create("1180496378418302976", "1180692054221656064");
        Assert.assertNotNull(order);
    }

    @Test
    public void pay() {
    }

    @Test
    public void aliCallback() {
    }

    @Test
    public void queryOrderPayStatus() {
    }

    @Test
    public void cancel() {
    }

    @Test
    public void orderList() {
    }

    @Test
    public void getById() {
    }

    @Test
    public void receipt() {
    }
}
