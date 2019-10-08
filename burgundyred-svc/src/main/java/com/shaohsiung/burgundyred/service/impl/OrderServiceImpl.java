package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.controller.request.Response;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.dto.CartItem;
import com.shaohsiung.burgundyred.enums.OrderState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.mapper.OrderMapper;
import com.shaohsiung.burgundyred.model.Order;
import com.shaohsiung.burgundyred.model.OrderItem;
import com.shaohsiung.burgundyred.service.CartService;
import com.shaohsiung.burgundyred.service.OrderService;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Transactional
@Service(version = "1.0.0")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    @Reference(version = "1.0.0")
    private ProductService productService;

    @Reference(version = "1.0.0")
    private CartService cartService;

    /**
     * 创建订单
     *
     * @param userId
     * @param shippingId
     * @return
     */
    @Override
    public Order create(String userId, String shippingId) {

        // 获取购物车，判断购物车存在商品
        Cart cart = cartService.get(userId);
        if (cart.getContent().size() <= 0) {
            throw new FrontEndException("购物车空空如也");
        }

        // 创建订单对象
        Order order = Order.builder().id(idWorker.nextId() + "")
                .state(OrderState.UNPAID)
                .userId(userId)
                .shippingId(shippingId)
                .orderNo(idWorker.nextId() + "")
                .total(cart.getTotal())
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        orderMapper.save(order);

        // 创建订单项对象
        Map<String, CartItem> cartItemMap = cart.getContent();
        for (Map.Entry<String, CartItem> cartItemEntry : cartItemMap.entrySet()) {
            CartItem cartItem = cartItemEntry.getValue();
            OrderItem orderItem = new OrderItem();

            BeanUtils.copyProperties(cartItem, orderItem);
            orderItem.setId(idWorker.nextId()+"");
            orderItem.setOrderId(order.getId());

            //orderItemMapper.save(orderItem);
        }


        // 清空购物车

        return null;
    }

    /**
     * 支付订单
     *
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    @Override
    public Response pay(String orderNo, String userId, String path) {
        return null;
    }

    /**
     * 支付回调
     *
     * @param params
     * @return
     */
    @Override
    public Response aliCallback(Map<String, String> params) {
        return null;
    }

    /**
     * 查询订单状态
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public Response queryOrderPayStatus(String userId, String orderNo) {
        return null;
    }

    /**
     * 取消订单 判断订单状态机 防止越权访问
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public Order cancel(String orderId, String userId) {
        return null;
    }

    /**
     * 获取用户订单列表
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<Order> orderList(String userId, int pageNum, int pageSize) {
        return null;
    }

    /**
     * 根据订单id查询订单详情
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public Order getById(String orderId, String userId) {
        return null;
    }

    /**
     * 收货
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public Order receipt(String orderId, String userId) {
        return null;
    }
}
