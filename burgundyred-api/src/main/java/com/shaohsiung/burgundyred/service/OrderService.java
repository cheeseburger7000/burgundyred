package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.dto.OrderDetailDto;
import com.shaohsiung.burgundyred.model.Order;

import java.util.List;
import java.util.Map;

/**
 * 订单服务
 */
public interface OrderService {

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    Order create(String userId, String shippingId);

    /**
     * 支付订单
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    Response pay(String orderNo, String userId, String path);

    /**
     * 支付回调
     * @param params
     * @return
     */
    Response aliCallback(Map<String, String> params);

    /**
     * 查询订单状态
     * @param userId
     * @param orderNo
     * @return
     */
    Response queryOrderPayStatus(String userId, String orderNo);

    /**
     * 取消订单 判断订单状态机 防止越权访问
     * @param orderId
     * @param userId
     * @return
     */
    Order cancel(String orderId, String userId);

    /**
     * 获取用户订单列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Order> orderList(String userId, int pageNum, int pageSize);

    /**
     * 根据订单id查询订单详情
     * @param orderId
     * @param userId
     * @return
     */
    OrderDetailDto getById(String orderId, String userId);

    /**
     * 收货
     * @param orderId
     * @param userId
     * @return
     */
    Order receipt(String orderId, String userId);
}
