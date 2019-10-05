package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper {

    @Select("select * from t_category order by create_time desc, hot desc")
    List<Category> getAll();

    @Insert("insert into t_category(id, name, detail, hot, create_time, update_time) " +
            "values(#{id}, #{name}, #{detail}, #{hot}, #{createTime}, #{updateTime})")
    int save(Category category);
}
