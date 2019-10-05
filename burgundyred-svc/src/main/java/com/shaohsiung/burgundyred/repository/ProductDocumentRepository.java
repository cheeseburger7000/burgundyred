package com.shaohsiung.burgundyred.repository;

import com.shaohsiung.burgundyred.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {
}
