package com.shaohsiung.burgundyred.service;

import com.shaohsiung.burgundyred.document.ProductDocument;

import java.util.List;

/**
 * 商品搜索模块
 */
public interface SearchService {
    /**
     * 全文高亮搜索
     *      范围字段：商品名称，商品详情
     * @param keyword
     * @return
     */
    List<ProductDocument> search(String keyword);
}
