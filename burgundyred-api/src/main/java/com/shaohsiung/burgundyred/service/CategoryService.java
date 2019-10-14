package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.dto.CategoryListItemDto;
import com.shaohsiung.burgundyred.model.Category;

import java.util.List;

/**
 * 商品类目模块
 */
public interface CategoryService {

    /**
     * 获取商品类目列表
     * 后台
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Category> categoryList(Integer pageNum, Integer pageSize);

    /**
     * 添加商品类目
     * 后台
     * @param category
     * @return
     */
    Category addCategory(Category category);

    /**
     * 获取主页热门分类列表
     * 前台
     * @return
     */
    List<CategoryListItemDto> indexCategoryList();

    /**
     * 设置商品类目为热门
     *
     * 后台
     * @param categoryId
     * @return
     */
    BaseResponse setHot(String categoryId);

    /**
     * 商品类目取消热门
     *
     * 后台
     * @param categoryId
     * @return
     */
    BaseResponse setUnhot(String categoryId);

    /**
     * 商品类目列表总记录数
     * @return
     */
    Integer categoryListTotalRecord();
}
