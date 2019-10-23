package com.shaohsiung.burgundyred.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shaohsiung.burgundyred.document.ProductDocument;
import com.shaohsiung.burgundyred.model.Product;
import com.shaohsiung.burgundyred.repository.ProductDocumentRepository;
import com.shaohsiung.burgundyred.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service(version = "1.0.0")
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ProductDocumentRepository productDocumentRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 全文高亮搜索
     * 范围字段：商品名称，商品详情
     *
     * @param keyword
     * @return
     */
    @Override
    public List<ProductDocument> search(String keyword) {
        List<ProductDocument> result = null;

        // 取消高亮搜索分页 1
//        Pageable pageable = PageRequest.of(pageNum, pageSize);

        String preTag = "<font color='#dd4b39'>";
        String postTag = "</font>";

        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                //withQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("address", keyword))).
                        withQuery(QueryBuilders.multiMatchQuery(keyword, "name", "detail")).
                        withHighlightFields(
                                new HighlightBuilder.Field("name").preTags(preTag).postTags(postTag),
                                new HighlightBuilder.Field("detail").preTags(preTag).postTags(postTag)
                        ).build();

        // 取消高亮搜索分页 2
//        searchQuery.setPageable(pageable);

        AggregatedPage<ProductDocument> productDocumentList = elasticsearchTemplate.queryForPage(searchQuery, ProductDocument.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<ProductDocument> chunk = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }

                    ProductDocument productDocument = new ProductDocument();
                    // 1.获取基本字段
                    Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                    productDocument.setId(searchHit.getId());
                    productDocument.setMainPicture((String) sourceAsMap.get("mainPicture"));
                    // FIXME 处理 BigDecimal 和 Double 转换
                    productDocument.setPrice(new BigDecimal((Integer) sourceAsMap.get("price")));

                    // 2.获取高亮字段
                    Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    HighlightField name = highlightFields.get("name");
                    if (name != null) {
                        productDocument.setName(name.fragments()[0].toString());
                    } else {
                        productDocument.setName((String) sourceAsMap.get("name"));
                    }
                    HighlightField detail = highlightFields.get("detail");
                    if (detail != null) {
                        productDocument.setDetail(detail.fragments()[0].toString());
                    } else {
                        productDocument.setDetail((String) sourceAsMap.get("detail"));
                    }

                    chunk.add(productDocument);
                }
                if (chunk.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) chunk);
                }
                return null;
            }
        });

        if (productDocumentList != null) {
            result = productDocumentList.getContent();
        } else {
            result = Collections.emptyList();
        }


        return result;
    }
}
