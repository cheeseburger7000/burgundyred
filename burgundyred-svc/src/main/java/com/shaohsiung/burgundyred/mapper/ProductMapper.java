package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ProductMapper {

    @Select("select * from t_product where category_id = #{categoryId} order by create_time desc")
    List<Product> getProductListByCategoryId(@Param("categoryId") String categoryId, RowBounds rowBounds);

    @Select("select * from t_product where id = #{productId}")
    Product getProductById(@Param("productId") String productId);

    @Insert("insert into t_product(id, name, detail, price, stock, state, main_picture, sub_picture, category_id, create_time, update_time) " +
            "values(#{id}, #{name}, #{detail}, #{price}, #{stock}, #{state}, #{mainPicture}, #{subPicture}, #{categoryId}, #{createTime}, #{updateTime})")
    int save(Product product);
}
