package com.shaohsiung.burgundyred.handler;

import com.shaohsiung.burgundyred.enums.OrderState;
import com.shaohsiung.burgundyred.enums.PayPlatformEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PayPlatformEnumTypeHandler extends BaseTypeHandler<PayPlatformEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PayPlatformEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    /**
     * @param rs
     * @param columnName Colunm name, when configuration <code>useColumnLabel</code> is <code>false</code>
     */
    @Override
    public PayPlatformEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseOrderStatus(rs.getInt(columnName));
    }

    @Override
    public PayPlatformEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseOrderStatus(rs.getInt(columnIndex));
    }

    @Override
    public PayPlatformEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseOrderStatus(cs.getInt(columnIndex));
    }

    private PayPlatformEnum parseOrderStatus(Integer value) {
        if (value == 0) {
            return PayPlatformEnum.WECHAT;
        } else if (value == 1) {
            return PayPlatformEnum.ALIPAY;
        }
        throw new RuntimeException("支付平台状态转换错误");
    }
}
