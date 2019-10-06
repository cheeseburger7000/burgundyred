package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.dto.Cart;

/**
 * 购物车模块
 */
public interface CartService {

    /**
     * 获取用户购物车
     * @param userId
     * @return
     */
    Cart get(String userId);

    /**
     * 添加商品到购物车
     * @param productId
     * @param userId
     * @return
     */
    Cart add(String productId, String userId);

    /**
     * 购物车商品项-1
     * @param productId
     * @param userId
     * @return
     */
    Cart decrease(String productId, String userId);

    /**
     * 购物车商品项+1
     * @param productId
     * @param userId
     * @return
     */
    Cart increase(String productId, String userId);

    /**
     * 移除购物车商品项
     * @param productId
     * @param userId
     * @return
     */
    Cart deleteCartItem(String productId, String userId);

    /**
     * 修改购物车商品项数量
     *
     * 注意库存。。。
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    Cart updateCartItemCount(String userId, String productId, Integer count);

    /**
     * 清空购物车
     * @param userId
     * @return
     */
    Cart clear(String userId);

}
