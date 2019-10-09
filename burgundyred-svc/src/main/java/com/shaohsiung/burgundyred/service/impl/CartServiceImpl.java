package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.dto.Cart;
import com.shaohsiung.burgundyred.dto.CartItem;
import com.shaohsiung.burgundyred.enums.ProductState;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.FrontEndException;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.service.CartService;
import com.shaohsiung.burgundyred.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service(version = "1.0.0")
public class CartServiceImpl implements CartService {

    private final static String CART_KEY = "BURGUNDYRED_CART";

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate<String, Cart> redisTemplate;

    /**
     * 获取用户购物车
     *
     * @param userId
     * @return
     */
    @Override
    public Cart get(String userId) {
        Cart result = null;
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        // reids中存在购物车
        if (redisTemplate.hasKey(CART_KEY) && hashOperations.hasKey(CART_KEY , userId)) {
            result = Optional.of(hashOperations.get(CART_KEY, userId)).get();
            log.info("【购物车模块】从redis获取购物车 {}", result);
            return result;
        }

        // 用户还没有购物车
        result = Cart.builder().userId(userId).content(new HashMap<>()).total(new BigDecimal(BigInteger.ZERO)).build();
        hashOperations.put(CART_KEY, userId, result);
        log.info("【购物车模块】保存购物车 {} 到redis", result);
        return result;
    }

    /**
     * 添加商品到购物车
     *
     * @param productId
     * @param userId
     * @return
     */
    @Override
    public Cart add(String productId, String userId) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        // 根据id获取商品， 判断商品是否上架
        Product product = productService.getProductById(productId);
        if (product.getState().equals(ProductState.HAS_BEEN_REMOVED)) {
            throw new FrontEndException("该商品已下架");
        }

        // 判断库存
        Integer stock = productService.getStockByProductId(productId);
        if (stock <= 0) {
            throw new FrontEndException("商品库存不足");
        }

        // 根据userId获取购物车，判断购物车中是否已经存在该商品
        Cart cart = get(userId);
        hashOperations.delete(CART_KEY, userId); // 处理反序列化异常

        Map<String, CartItem> content = cart.getContent();
        for (Map.Entry<String, CartItem> entry: content.entrySet()) {
            String currentProductId = entry.getKey();

            if (productId != null && productId.equals(currentProductId)) {
                CartItem cartItem = entry.getValue();
                cart.getContent().remove(cartItem);
                // 若存在 数量+1 计算购物车总价
                cartItem.setQuantity(cartItem.getQuantity()+1);
                cartItem.setAmount(cartItem.getAmount().add(product.getPrice()));

                cart.getContent().put(productId, cartItem);
                cart.setTotal(cart.getTotal().add(product.getPrice()));

                // 将购物车更改到redis
                hashOperations.put(CART_KEY, userId, cart);
                log.info("【购物车模块】添加商品 {} 到购物车 {} 到redis", product, cart);
                return  cart;
            }
        }

        // 若不存在 创建购物车项目 计算购物车总价
        CartItem newCartItem = CartItem.builder().productId(productId)
                .price(product.getPrice())
                .amount(product.getPrice())
                .quantity(1)
                .name(product.getName())
                .build();
        cart.getContent().put(productId, newCartItem);
        cart.setTotal(cart.getTotal().add(product.getPrice()));

        // 将购物车更改到redis
        hashOperations.put(CART_KEY, userId, cart);
        log.info("【购物车模块】添加商品 {} 到购物车 {} 到redis", productId, cart);
        return cart;
    }

    /**
     * 购物车商品项-1
     *
     * @param productId
     * @param userId
     * @return
     */
    @Override
    public Cart decrease(String productId, String userId) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();
        Product product = productService.getProductById(productId);

        // 获取购物车
        Cart cart = get(userId);
        hashOperations.delete(CART_KEY, userId);

        // 判断购物车是否存在该商品，不存在就抛异常
        Map<String, CartItem> content = cart.getContent();
        for (Map.Entry<String, CartItem> entry: content.entrySet()) {
            String currentProductId = entry.getKey();
            if (productId != null && productId.equals(currentProductId)) {
                // 删除目标商品 -1
                CartItem cartItem = entry.getValue();
                if (cartItem.getQuantity() > 0) {
                    cartItem.setAmount(cartItem.getAmount().subtract(product.getPrice()));
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    if (cartItem.getQuantity() == 0) {
                        content.remove(productId);
                    }
                    cart.setTotal(cart.getTotal().subtract(product.getPrice()));
                    // 存储到redis
                    hashOperations.put(CART_KEY, userId, cart);
                    log.info("【购物车模块】购物车商品-1 productId:{}， cart:{}", productId, cart);
                    return cart;
                }
            }
        }
        hashOperations.put(CART_KEY, userId, cart);
        log.error("【购物车模块】购物车商品-1失败 productId:{}，userId:{}", productId, userId);
        throw new BackEndException("购物车中不存在该商品");
    }

    /**
     * TODO 购物车商品项+1
     *
     * @param productId
     * @param userId
     * @return
     */
    @Override
    public Cart increase(String productId, String userId) {
        return null;
    }

    /**
     * 移除购物车商品项
     *
     * @param productId
     * @param userId
     * @return
     */
    @Override
    public Cart deleteCartItem(String productId, String userId) {
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        // 根据userId获取购物车，判断购物车中是否已经存在该商品
        Cart cart = get(userId);
        hashOperations.delete(CART_KEY, userId);

        Map<String, CartItem> content = cart.getContent();
        for (Map.Entry<String, CartItem> entry: content.entrySet()) {
            String currentProductId = entry.getKey();
            if (productId != null && productId.equals(currentProductId)) {
                CartItem cartItem = entry.getValue();

                cart.getContent().remove(productId);
                cart.setTotal(cart.getTotal().subtract(cartItem.getAmount()));

                hashOperations.put(CART_KEY, userId, cart);
                log.info("【购物车模块】删除购物车商品 {} 购物车 {}", productId, cart);
                return cart;
            }
        }

        // 将购物车放回redis
        hashOperations.put(CART_KEY, userId, cart);
        log.error("【购物车模块】删除购物车商品失败 productId:{}，userId:{}", productId, userId);
        throw new FrontEndException("购物车中不存在该商品");
    }

    /**
     * 修改购物车商品项数量
     *
     * 检查库存。。。
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public Cart updateCartItemCount(String userId, String productId, Integer count) {
        return null;
    }

    /**
     * 清空购物车
     *
     * @param userId
     * @return
     */
    @Override
    public Cart clear(String userId) {
        Cart result = null;
        HashOperations<String, String, Cart> hashOperations = redisTemplate.opsForHash();

        // 清空redis中的购物车
        if (redisTemplate.hasKey(CART_KEY ) && hashOperations.hasKey(CART_KEY , userId)) {
            hashOperations.delete(CART_KEY, userId);
        }

        result = Cart.builder().userId(userId)
                .total(new BigDecimal(0))
                .content(new HashMap<>())
                .build();
        hashOperations.put(CART_KEY, userId, result);
        return result;
    }
}
