package com.shaohsiung.burgundyred.handler;

import com.shaohsiung.burgundyred.enums.OrderState;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderStateTypeHandler extends BaseTypeHandler<OrderState> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, OrderState orderStatus, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, orderStatus.getCode());
    }

    @Override
    public OrderState getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return parseOrderStatus(resultSet.getInt(s));
    }

    @Override
    public OrderState getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return parseOrderStatus(resultSet.getInt(i));
    }

    @Override
    public OrderState getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return parseOrderStatus(callableStatement.getInt(i));
    }

    private OrderState parseOrderStatus(Integer value) {
        if (value == 0) {
            return OrderState.UNPAID;
        } else if (value == 1) {
            return OrderState.NOT_SHIPPED;
        } else if (value == 2) {
            return OrderState.CANCEL;
        } else if (value == 3) {
            return OrderState.SHIPPED;
        } else if (value == 4) {
            return OrderState.COMPLETED;
        } else if (value == 5) {
            return OrderState.CLOSED;
        }
        throw new RuntimeException("订单状态转换错误");
    }
}