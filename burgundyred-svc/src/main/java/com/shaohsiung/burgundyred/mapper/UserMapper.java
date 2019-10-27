package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface UserMapper {

    @Insert("insert into t_user(id, user_name, password, email, avatar, mobile, state, create_time) " +
            "values(#{id}, #{userName}, #{password}, #{email}, #{avatar}, #{mobile}, #{state}, #{createTime}) ")
    int save(User user);

    @Select("select * from t_user where user_name = #{userName} and password = #{password}")
    User findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String encryptPassword);

    @Update("update t_user set state = 1 where id = #{userId}")
    int activate(@Param("userId") String userId);

    @Select("select user_name from t_user where user_name = #{userName}")
    String confirmUserNameUnique(@Param("userName") String userName);

    @Select("select * from t_user order by state desc, create_time desc")
    List<User> userList(RowBounds rowBounds);

    @Select("select count(1) from t_user")
    Integer userListTotalRecord();

    @Update("update t_user set state = 2 where id = #{userId}")
    int freeze(@Param("userId") String userId);

    @Update("update t_user set state = 1 where id = #{userId}")
    int normal(String userId);

    @Select("select * from t_user where id = #{userId}")
    User getById(@Param("userId") String userId);
}
