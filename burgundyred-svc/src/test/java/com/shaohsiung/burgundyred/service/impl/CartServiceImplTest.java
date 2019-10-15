package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.service.CartService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CartServiceImplTest {
    @Reference(version = "1.0.0")
    private CartService cartService;

    @Test
    public void get() {
        Cart cart = cartService.get("1182493800958922752");
    }

    @Test
    public void add() {
        cartService.add("1182854430526148608", "1182493800958922752");
        cartService.add("1182854430526148608", "1182493800958922752");
        Cart add3 = cartService.add("1182854430526148608", "1182493800958922752");
        Assert.assertNotEquals(0, add3.getContent().size());
    }

    @Test
    public void decrease() {
        Cart before = cartService.get("1180496378418302976");
        Cart after = cartService.decrease("2", "1180496378418302976");
    }

    @Test
    public void increase() {
    }

    @Test
    public void deleteCartItem() {
        Cart cart = cartService.deleteCartItem("1", "1180496378418302976");
    }

    @Test
    public void updateCartItemCount() {
    }

    @Test
    public void clear() {
        Cart clear = cartService.clear("1180496378418302976");
    }
}
