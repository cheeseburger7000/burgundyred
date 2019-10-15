package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.PayInfo;
import org.apache.ibatis.annotations.Insert;

public interface PayInfoMapper {
    @Insert("insert into t_pay_info(id, user_id, order_no, pay_platform, platform_number, platform_status, create_time, update_time) " +
            "values(#{id}, #{userId}, #{orderNo}, #{payPlatform}, #{platformNumber}, #{platformStatus}, #{createTime}, #{updateTime})")
    int insert(PayInfo payInfo);
}
