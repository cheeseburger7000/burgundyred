package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface OrderMapper {

    @Insert("insert into t_order(id, order_no, total, state, user_id, shipping_id, create_time, update_time) " +
            "values(#{id}, #{orderNo}, #{total}, #{state}, #{userId}, #{shippingId}, #{createTime}, #{updateTime})")
    int save(Order order);

    @Select("select * from t_order where id = #{orderId}")
    Order findById(@Param("orderId") String id);

    @Select("select * from t_order where id = #{orderId} and user_id = #{userId}")
    Order getByIdAndUserId(@Param("orderId") String orderId, @Param("userId") String userId);

    @Update("update t_order set state = #{state} where id = #{id}")
    int update(Order order);

    @Select("select * from t_order where user_id = #{userId} order by state desc, create_time desc")
    List<Order> userOrderList(@Param("userId") String userId, RowBounds rowBounds);

    @Select("select count(1) from t_order where user_id = #{userId}")
    Integer userOrderListTotalRecord(String userId);

    @Select("select * from t_order order by state desc, create_time desc")
    List<Order> orderList(RowBounds rowBounds);

    @Select("select count(1) from t_order")
    Integer orderListTotalRecord();

    @Select("select * from t_order where user_id = #{userId} and order_no = #{order_no}")
    Order selectByUserIdAndOrderNo(@Param("userId") String userId, @Param("orderNo") String orderNo);

    @Select("select * from t_order where order_no = #{orderNo}")
    Order getByOrderNo(@Param("orderNo") String orderNo);
}
