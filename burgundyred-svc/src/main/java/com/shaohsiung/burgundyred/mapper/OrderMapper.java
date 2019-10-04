package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OrderMapper {

    @Insert("insert into t_order(id, order_no, total, state, user_id, shipping_id, create_time, update_time) " +
            "values(#{id}, #{orderNo}, #{total}, #{state}, #{userId}, #{shippingId}, #{createTime}, #{updateTime})")
    int save(Order order);

    @Select("select * from t_order where id = #{orderId}")
    Order findById(@Param("orderId") String id);
}
