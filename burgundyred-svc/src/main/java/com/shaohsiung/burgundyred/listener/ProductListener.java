package com.shaohsiung.burgundyred.listener;

import com.shaohsiung.burgundyred.document.ProductDocument;
import com.shaohsiung.burgundyred.repository.ProductDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.shaohsiung.burgundyred.converter.ObjectBytesConverter.getObjectFromBytes;

@Slf4j
@Component
public class ProductListener {
    @Autowired
    private ProductDocumentRepository productDocumentRepository;

    @RabbitListener(queues = "topic.messages")
    public void addGoodsToElasticSearch(byte[] bytes) throws Exception{
        ProductDocument productDocument = (ProductDocument) getObjectFromBytes(bytes);
        productDocumentRepository.save(productDocument);
        log.info("监听器同步索引库：{}", productDocument);
    }
}
