package com.shaohsiung.burgundyred.mapper;

import com.shaohsiung.burgundyred.model.Banner;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface BannerMapper {

    @Select("select * from t_banner order by active desc, create_time desc")
    List<Banner> bannerList(RowBounds rowBounds);

    @Insert("insert into t_banner(id, name, path, active, create_time) values(#{id}, #{name}, #{path}, #{active}, #{createTime})")
    int save(Banner banner);

    @Delete("delete from t_banner where id = #{bannerId}")
    int deleteById(String bannerId);

    @Update("update t_banner set active = 1 where id = #{bannerId}")
    int active(String bannerId);

    @Select("select count(1) from t_banner where active = 1")
    Integer calcActiveCount();

    @Update("update t_banner set active = 0 where id = #{bannerId}")
    int inactive(String bannerId);
}
