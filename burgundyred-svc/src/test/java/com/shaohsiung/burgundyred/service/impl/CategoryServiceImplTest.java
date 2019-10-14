package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.model.Category;
import com.shaohsiung.burgundyred.service.CategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryServiceImplTest {
    @Reference(version = "1.0.0")
    private CategoryService categoryService;

    @Test
    public void categoryList() {
    }

    @Test
    public void addCategory() {
//        categoryListOne();
        categoryListSecond();
    }

    private void categoryListSecond() {
        Category c6 = Category.builder().name("千元有找高分好酒")
                .detail("千元有找高分好酒分类描述")
                .build();
        Category c7 = Category.builder().name("聚餐派對好酒！")
                .detail("聚餐派對好酒分类描述")
                .build();
        Category c8 = Category.builder().name("紐西蘭風雲酒莊絕佳功力!")
                .detail("紐西蘭風雲酒莊絕佳功力分类描述")
                .build();
        Category c9 = Category.builder().name("西班牙-里奧哈精选款式")
                .detail("西班牙-里奧哈精选款式分类描述")
                .build();
        Category c10 = Category.builder().name("卡本內蘇維濃品种！")
                .detail("卡本內蘇維濃品种分类描述")
                .build();
        categoryService.addCategory(c6);
        categoryService.addCategory(c7);
        categoryService.addCategory(c8);
        categoryService.addCategory(c9);
        categoryService.addCategory(c10);
    }

    private void categoryListOne() {
        Category c1 = Category.builder().name("清酒之王十四代品种!")
                .detail("清酒之王十四代品种分类描述")
                .build();
        Category c2 = Category.builder().name("稀有年份香檳王搶購中！")
                .detail("稀有年份香檳王搶購中分类描述")
                .build();
        Category c3 = Category.builder().name("紐西蘭風雲酒莊絕佳功力!")
                .detail("紐西蘭風雲酒莊絕佳功力分类描述")
                .build();
        Category c4 = Category.builder().name("新手必喝七大经典品种！")
                .detail("新手必喝七大经典品种分类描述")
                .build();
        Category c5 = Category.builder().name("布根地入門指南！")
                .detail("布根地入門指南分类描述")
                .build();
        categoryService.addCategory(c1);
        categoryService.addCategory(c2);
        categoryService.addCategory(c3);
        categoryService.addCategory(c4);
        categoryService.addCategory(c5);
    }
}
