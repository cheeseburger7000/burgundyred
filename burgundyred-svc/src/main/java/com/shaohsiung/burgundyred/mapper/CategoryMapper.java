package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CategoryMapper {

    @Select("select * from t_category order by create_time desc, hot desc")
    List<Category> getAll(RowBounds rowBounds);

    @Insert("insert into t_category(id, name, detail, hot, create_time, update_time) " +
            "values(#{id}, #{name}, #{detail}, #{hot}, #{createTime}, #{updateTime})")
    int save(Category category);

    @Select("select * from t_category where hot = 1 order by create_time desc limit 10")
    List<Category> indexCategoryList();

    @Update("update t_category set hot = 1 where id = #{categoryId}")
    int setHot(@Param("categoryId") String categoryId);

    @Update("update t_category set hot = 0 where id = #{categoryId}")
    int setUnhot(@Param("categoryId") String categoryId);

    @Select("select count(1) from t_category")
    Integer categoryListTotalRecord();
}
