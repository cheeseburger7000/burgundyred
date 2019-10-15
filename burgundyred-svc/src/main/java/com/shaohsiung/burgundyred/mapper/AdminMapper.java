package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Administrator;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AdminMapper {
    @Select("select * from t_admin where admin_name = #{adminName} and password = #{password}")
    Administrator findByAdminNameAndPassword(@Param("adminName") String adminName, @Param("password") String encryptPassword);
}
