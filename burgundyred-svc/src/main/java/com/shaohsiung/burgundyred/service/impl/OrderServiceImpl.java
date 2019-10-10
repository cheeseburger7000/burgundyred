package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.dto.CartItem;
import com.shaohsiung.burgundyred.dto.OrderDetailDto;
import com.shaohsiung.burgundyred.enums.OrderState;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.mapper.OrderItemMapper;
import com.shaohsiung.burgundyred.mapper.OrderMapper;
import com.shaohsiung.burgundyred.model.Order;
import com.shaohsiung.burgundyred.model.OrderItem;
import com.shaohsiung.burgundyred.model.Shipping;
import com.shaohsiung.burgundyred.service.CartService;
import com.shaohsiung.burgundyred.service.OrderService;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.service.ShippingService;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Transactional
@Service(version = "1.0.0")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShippingService shippingService;

    @Autowired
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

            orderItemMapper.save(orderItem);
        }

        // 清空购物车
        cartService.clear(userId);

        return order;
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
    public BaseResponse pay(String orderNo, String userId, String path) {
        return null;
    }

    /**
     * 支付回调
     *
     * @param params
     * @return
     */
    @Override
    public BaseResponse aliCallback(Map<String, String> params) {
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
    public BaseResponse queryOrderPayStatus(String userId, String orderNo) {
        return null;
    }

    /**
     * 取消订单
     *
     * 判断订单状态机
     * 防止越权访问
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public Order cancel(String orderId, String userId) {
        Order order = orderMapper.getByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new FrontEndException("订单不存在");
        }

        OrderState state = order.getState();
        if (state.equals(OrderState.UNPAID)) {
            order.setState(OrderState.CLOSED);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户取消订单，订单状态：未支付->已关闭，订单号：{}", orderId);
                return order;
            }
        } else if (state.equals(OrderState.NOT_SHIPPED)) {
            // TODO 退款
            // TODO 库存
            order.setState(OrderState.CLOSED);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户取消订单，订单状态：未发货->已关闭，订单号：{}", orderId);
                return order;
            }
        } else if (state.equals(OrderState.SHIPPED)) {
            order.setState(OrderState.CANCEL);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户取消订单，订单状态：已发货->已取消，订单号：{}", orderId);
                return order;
            }
        } else {
            log.warn("【订单服务】用户取消订单，订单状态异常，订单号：{}", orderId);
            throw new FrontEndException("该订单无法关闭");
        }

        throw new FrontEndException("订单取消失败");
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
        int offset = pageNum * pageSize;
        List<Order> orderList= orderMapper.orderList(userId, new RowBounds(offset, pageSize));
        return orderList;
    }

    /**
     * 根据订单id查询订单详情
     *      包括 订单信息 订单项信息 物流信息
     *
     * @param orderId
     * @param userId
     * @return
     */
    @Override
    public OrderDetailDto getById(String orderId, String userId) {
        Order order = orderMapper.getByIdAndUserId(orderId, userId);
        List<OrderItem> orderItemList = orderItemMapper.getOrderItemListByOrderId(orderId);
        Shipping shipping = shippingService.getById(order.getShippingId());

        OrderDetailDto orderDetailDto = new OrderDetailDto();
        BeanUtils.copyProperties(order, orderDetailDto);
        orderDetailDto.setOrderItemList(orderItemList);
        orderDetailDto.setShipping(shipping);
        return orderDetailDto;
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
        Order order = orderMapper.getByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new FrontEndException("订单不存在");
        }

        OrderState state = order.getState();
        if (state.equals(OrderState.SHIPPED)) {
            order.setState(OrderState.COMPLETED);
            int update = orderMapper.update(order);
            if (update == 1) {
                log.info("【订单服务】用户收货，订单状态机：已发货->已完成，订单号：{}", orderId);
                return order;
            }
        }
        throw new FrontEndException("订单状态机转化错误");
    }
}
