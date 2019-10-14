package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.dto.CategoryListItemDto;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.mapper.CategoryMapper;
import com.shaohsiung.burgundyred.model.Category;
import com.shaohsiung.burgundyred.service.CategoryService;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service(version = "1.0.0")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取商品类目列表
     * 前台 无序分页，创建时间等字段
     *
     * @return
     */
    @Override
    public List<Category> categoryList() {
        List<Category> result = categoryMapper.getAll();
        log.info("获取商品类目列表：{}", result);
        return result;
    }

    /**
     * 添加商品类目
     * 后台
     *
     * @param category
     * @return
     */
    @Transactional
    @Override
    public Category addCategory(Category category) {
        // 初始化默认值
        category.setHot(false);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        category.setId(idWorker.nextId()+"");

        int save = categoryMapper.save(category);
        if (save == 1) {
            log.info("添加商品类目：{}", category);
            return category;
        }
        throw new BackEndException(ErrorState.CATEGORY_CREATE_FAILED);
    }

    /**
     * 获取主页热门分类列表
     *
     * @return
     */
    @Override
    public List<CategoryListItemDto> indexCategoryList() {
        List<Category> categories = categoryMapper.indexCategoryList();
        List<CategoryListItemDto> result = categories.stream().map(category -> {
            CategoryListItemDto categoryListItemDto = new CategoryListItemDto();
            BeanUtils.copyProperties(category, categoryListItemDto);
            return categoryListItemDto;
        }).collect(Collectors.toList());
        log.info("【商品类目基础SVC】获取主页分类推荐列表：{}", result);
        return result;
    }
}
