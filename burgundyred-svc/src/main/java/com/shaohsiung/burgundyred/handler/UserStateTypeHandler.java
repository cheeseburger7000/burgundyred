package com.shaohsiung.burgundyred.handler;

import com.shaohsiung.burgundyred.enums.UserState;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStateTypeHandler extends BaseTypeHandler<UserState> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, UserState userStatus, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, userStatus.getCode());
    }

    @Override
    public UserState getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return parseUserStatus(resultSet.getInt(s));
    }

    @Override
    public UserState getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return parseUserStatus(resultSet.getInt(i));
    }

    @Override
    public UserState getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return parseUserStatus(callableStatement.getInt(i));
    }

    private UserState parseUserStatus(Integer value) {
        if (value == 0) {
            return UserState.INACTIVATED;
        } else if (value == 1) {
            return UserState.NORMAL;
        } else if (value == 2) {
            return UserState.FREEZE;
        }
        throw new RuntimeException("用户状态转换错误");
    }
}
