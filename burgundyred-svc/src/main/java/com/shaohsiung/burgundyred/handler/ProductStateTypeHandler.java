package com.shaohsiung.burgundyred.handler;

import com.shaohsiung.burgundyred.enums.ProductState;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductStateTypeHandler extends BaseTypeHandler<ProductState> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, ProductState goodsStatus, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, goodsStatus.getCode());
    }

    @Override
    public ProductState getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return parseGoodsStatus(resultSet.getInt(s));
    }

    @Override
    public ProductState getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return parseGoodsStatus(resultSet.getInt(i));
    }

    @Override
    public ProductState getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return parseGoodsStatus(callableStatement.getInt(i));
    }

    private ProductState parseGoodsStatus(Integer value) {
        if (value == 0) {
            return ProductState.ON_THE_SHELF;
        } else if (value == 1) {
            return ProductState.HAS_BEEN_REMOVED;
        }
        throw new RuntimeException("商品状态转换错误"); // TODO 定义TypeHandleException
    }
}
