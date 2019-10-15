package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrderItemMapper {
    @Insert("insert into t_order_item(id, order_id, product_id, quantity, amount) " +
            "values(#{id}, #{orderId}, #{productId}, #{quantity}, #{amount})")
    void save(OrderItem orderItem);

    @Select("select * from t_order_item where order_id = #{id}")
    List<OrderItem> getOrderItemListByOrderId(String orderId);

    @Select("select * from t_order_item where order_no = #{orderNo}")
    List<OrderItem> listByOrderNo(@Param("orderNo") String orderNo);
}
