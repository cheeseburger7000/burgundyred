package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.dto.CategoryListItemDto;
import com.shaohsiung.burgundyred.model.Category;

import java.util.List;

/**
 * 商品类目模块
 */
public interface CategoryService {

    /**
     * 获取商品类目列表
     * 前台
     * @return
     */
    List<Category> categoryList();

    /**
     * 添加商品类目
     * 后台
     * @param category
     * @return
     */
    Category addCategory(Category category);

    /**
     * 获取主页热门分类列表
     * @return
     */
    List<CategoryListItemDto> indexCategoryList();
}
