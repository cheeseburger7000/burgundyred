package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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

    @Select("select stock from t_product where id = #{productId}")
    Integer getStockByProductId(@Param("productId") String productId);

    @Select("select * from t_product order by create_time desc limit #{limit}")
    List<Product> latestStyle(@Param("limit") int limit);

    @Select("select * from t_product limit #{limit}")
    List<Product> recommendedStyle(int limit);

    @Select("select * from t_product order by price asc limit #{limit}")
    List<Product> intimateStyle(int limit);

    @Select("select * from t_product order by stock desc limit #{limit}")
    List<Product> scarceStyle(int limit);

    @Select("select * from t_product order by stock asc limit #{limit}")
    List<Product> clearanceStyle(int limit);

    @Select("select * from t_product order by state desc, create_time desc")
    List<Product> sellerProductList(RowBounds rowBounds);

    @Select("select count(1) from t_product")
    Integer sellerProductListTotalRecord();

    @Update("update t_product set state = 0 where id = #{productId}")
    int onShelves(@Param("productId") String productId);

    @Update("update t_product set state = 1 where id = #{productId}")
    int remove(String productId);

    @Update("update t_product set stock = #{stock} where id = #{id}")
    int updateStock(Product product);

    @Select("select count(1) from t_product where category_id = #{categoryId}")
    Integer productTotalRecordByCategoryId(@Param("categoryId") String categoryId);
}
