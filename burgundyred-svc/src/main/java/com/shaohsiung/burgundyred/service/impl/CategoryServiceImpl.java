package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.api.BaseResponse;
import com.shaohsiung.burgundyred.constant.AppConstant;
import com.shaohsiung.burgundyred.dto.CategoryListItemDto;
import com.shaohsiung.burgundyred.error.BackEndException;
import com.shaohsiung.burgundyred.error.ErrorState;
import com.shaohsiung.burgundyred.mapper.CategoryMapper;
import com.shaohsiung.burgundyred.model.Category;
import com.shaohsiung.burgundyred.service.CategoryService;
import com.shaohsiung.burgundyred.util.BaseResponseUtils;
import com.shaohsiung.burgundyred.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
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
     * 后台 无序分页，创建时间等字段
     *
     * @return
     */
    @Override
    public List<Category> categoryList(Integer pageNum, Integer pageSize) {
        int offset = pageNum * pageSize;
        List<Category> result = categoryMapper.getAll(new RowBounds(offset, pageSize));
        log.info("【商品类目基础SVC】获取商品类目列表：{}", result);
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
            log.info("【商品类目基础SVC】添加商品类目：{}", category);
            return category;
        }
        throw new BackEndException(ErrorState.CATEGORY_CREATE_FAILED);
    }

    /**
     * 获取主页热门分类列表
     *
     * 前台
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

    /**
     * 设置商品类目为热门
     *
     * 后台
     * @param categoryId
     * @return
     */
    @Override
    public BaseResponse setHot(String categoryId) {
        Integer hotCount = categoryMapper.calcHotCount();
        if (hotCount >= AppConstant.MAX_CATEGORY_COUNT) {
            throw new BackEndException(ErrorState.BANNER_COUNT_REACHES_THE_UPPER_LIMIT);
        }

        int update = categoryMapper.setHot(categoryId);
        if (update == 1) {
            log.info("【商品类目基础SVC】商品类目设置热门成功，商品类目id：{}", categoryId);
            return BaseResponseUtils.success();
        }
        throw new BackEndException(ErrorState.CATEGORY_SET_HOT_FAILED);
    }

    /**
     * 商品类目取消热门
     *
     * 后台
     * @param categoryId
     * @return
     */
    @Override
    public BaseResponse setUnhot(String categoryId) {
        Integer hotCount = categoryMapper.calcHotCount();
        if (hotCount == AppConstant.MAX_CATEGORY_COUNT) {
            throw new BackEndException(ErrorState.CATEGORY_COUNT_REACHES_THE_LOWER_LIMIT);
        }

        int update = categoryMapper.setUnhot(categoryId);
        if (update == 1) {
            log.info("【商品类目基础SVC】商品类目取消热门成功，商品类目id：{}", categoryId);
            return BaseResponseUtils.success();
        }
        throw new BackEndException(ErrorState.CATEGORY_SET_UNHOT_FAILED);
    }

    /**
     * 商品类目列表总记录数
     *
     * @return
     */
    @Override
    public Integer categoryListTotalRecord() {
        return categoryMapper.categoryListTotalRecord();
    }

    @Override
    public Category getById(String categoryId) {
        return categoryMapper.getById(categoryId);
    }
}
