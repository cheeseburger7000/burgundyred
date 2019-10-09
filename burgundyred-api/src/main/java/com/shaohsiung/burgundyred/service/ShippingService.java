package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.model.Shipping;

import java.util.List;

/**
 * 物流模块
 */
public interface ShippingService {

    /**
     * 用户添加物流信息
     * @param shipping
     * @return
     */
    Shipping addShipping(Shipping shipping);

    /**
     * 获取用户物流信息
     * @param userId
     * @return
     */
    List<Shipping> shippingList(String userId);

    /**
     * 修改物流信息
     * @param shipping
     * @return
     */
    Shipping updateShipping(Shipping shipping);

    /**
     * 用户删除物流信息
     * @param ShippingId
     * @return
     */
    int deleteShipping(String ShippingId, String userId);

    /**
     * 获取物流详情
     * @param shippingId
     * @return
     */
    Shipping getById(String shippingId);
}
