package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.api.BaseResponse;
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
    BaseResponse pay(String orderNo, String userId, String path);

    /**
     * 支付回调
     * @param params
     * @return
     */
    BaseResponse aliCallback(Map<String, String> params);

    /**
     * 查询订单状态
     * @param userId
     * @param orderNo
     * @return
     */
    BaseResponse queryOrderPayStatus(String userId, String orderNo);

    /**
     * 用户退款
     * @param orderNo
     * @param userId
     * @return
     */
    BaseResponse refund(String orderNo, String userId);

    /**
     * 用户取消订单 判断订单状态机 防止越权访问
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
    List<Order> userOrderList(String userId, int pageNum, int pageSize);

    /**
     * 获取用户订单列表总数量
     * @param userId
     * @return
     */
    Integer userOrderListTotalRecord(String userId);

    /**
     * 根据订单id查询订单详情
     * @param orderId
     * @param userId
     * @return
     */
    OrderDetailDto getById(String orderId, String userId);

    /**
     * 买家收货
     * @param orderId
     * @param userId
     * @return
     */
    Order receipt(String orderId, String userId);

    /**
     * 卖家发货
     *
     * @param orderId
     * @return
     */
    Order ship(String orderId);

    /**
     * 卖家确认取消订单
     *
     * @param orderId
     * @return
     */
    Order confirmCancel(String orderId);

    /**
     * 卖家获取所有订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Order> orderList(int pageNum, int pageSize);

    /**
     * 获取订单列表总数量
     * @return
     */
    Integer orderListTotalRecord();

    Order getByOrderNo(String orderNo);
}
