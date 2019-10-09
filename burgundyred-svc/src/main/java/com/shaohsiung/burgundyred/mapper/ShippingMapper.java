package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Shipping;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ShippingMapper {
    @Insert("insert into t_shipping(id, user_id, " +
            "receiver_name, receiver_mobile, receiver_province, receiver_city, " +
            "receiver_district, receiver_address, receiver_zip, create_time, update_time) " +
            "values(#{id}, #{userId}, " +
            "#{receiverName}, #{receiverMobile}, #{receiverProvince}, #{receiverCity}, " +
            "#{receiverDistrict}, #{receiverAddress}, #{receiverZip}, #{createTime}, #{updateTime})")
    int save(Shipping shipping);

    @Select("select * from t_shipping where user_id = #{userId}")
    List<Shipping> getByUserId(@Param("userId") String userId);

    @Delete("delete from t_shipping where id = #{id} and user_id = #{userId}")
    int deleteByIdAndUserId(@Param("id") String shippingId, @Param("userId") String userId);

    @Select("select * from t_shipping where id = #{shippingId} and user_id = #{userId}")
    Shipping findByIdAndUserId(@Param("shippingId") String id, @Param("userId") String userId);

    @Update("update t_shipping set receiver_name = #{receiverName}, receiver_mobile = #{receiverMobile}, receiver_province = #{receiverProvince}, " +
            "receiver_city = #{receiverCity}, receiver_district = #{receiverDistrict}, receiver_address = #{receiverAddress}, receiver_zip = #{receiverZip}, " +
            "update_time = #{updateTime} where id = #{id} and user_id = #{userId}")
    int update(Shipping origin);

    @Select("select * from t_shipping where id = #{shippingId}")
    Shipping getById(String shippingId);
}
