package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.enums.OrderState;
import com.shaohsiung.burgundyred.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper {

    @Insert("insert into t_order(id, order_no, total, state, user_id, shipping_id, create_time, update_time) " +
            "values(#{id}, #{orderNo}, #{total}, #{state}, #{userId}, #{shippingId}, #{createTime}, #{updateTime})")
    int save(Order order);

    // FIXME 存在越权访问风险
    @Select("select * from t_order where id = #{orderId}")
    Order findById(@Param("orderId") String id);

    @Select("select * from t_order where id = #{orderId} and user_id = #{userId}")
    Order getByIdAndUserId(@Param("orderId") String orderId, @Param("userId") String userId);

    @Update("update t_order set state = #{state} where id = #{id}")
    int update(Order order);
}
