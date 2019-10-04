package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.enums.OrderState;
import com.shaohsiung.burgundyred.model.Order;
import com.shaohsiung.burgundyred.util.IdWorker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderMapperTest {
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void save() {
        Order order = Order.builder().id(idWorker.nextId()+"")
                .orderNo(idWorker.nextId()+"")
                .shippingId("0")
                .state(OrderState.SHIPPED)
                .userId("0")
                .total(BigDecimal.ZERO)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        int save = orderMapper.save(order);
        Assert.assertEquals(1, save);
    }

    @Test
    public void findById() {
        Order order = orderMapper.findById("1179957123610710016");
    }
}
