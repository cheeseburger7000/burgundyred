package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.OrderItem;
import org.apache.ibatis.annotations.Insert;

public interface OrderItemMapper {
    @Insert("insert into t_order_item(id, order_id, product_id, quantity, amount) " +
            "values(#{id}, #{orderId}, #{productId}, #{quantity}, #{amount})")
    void save(OrderItem orderItem);
}
