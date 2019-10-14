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

import java.util.List;

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
        Order order = orderService.create("1182493800958922752", "1180692054221656064");
        Assert.assertNotNull(order);
    }

    @Test(expected = FrontEndException.class)
    public void createEx() {
        Order order = orderService.create("1182493800958922752", "1180692054221656064");
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
        orderService.cancel("1181759454547415040", "1180496378418302976");
    }

    @Test(expected = RuntimeException.class)
    public void cancelEx() {
        orderService.cancel("1181759454547415040", "1180496378418302976");
    }

    @Test
    public void orderList() {
        List<Order> orderList = orderService.userOrderList("1", 0, 4);
    }

    @Test
    public void getById() {
    }

    @Test
    public void receipt() {
    }
}
