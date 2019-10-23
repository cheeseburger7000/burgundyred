package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shaohsiung.burgundyred.document.ProductDocument;
import com.shaohsiung.burgundyred.service.ProductService;
import com.shaohsiung.burgundyred.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchServiceImplTest {

    @Reference(version = "1.0.0")
    private SearchService searchService;

    @Test
    public void search() {
        List<ProductDocument> productDocuments = searchService.search("酒");
    }
}
