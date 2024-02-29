package com.skt.microservice.core;

import com.skt.common.exception.SKTException;
import com.skt.common.exception.external.InfrastructureException;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.microservice.persistence.ProductRepository;
import com.skt.microservice.persistence.entity.ProductEntity;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SelectProductServiceImpl implements SelectProductService {

    public static final String ERR_USER_MSG = "An error occurred while processing the message";

    private static final Logger LOG = LoggerFactory.getLogger(SelectProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public void processMessage(KafkaMessage kafkaMessage) {
        try {
            sendProducts(kafkaMessage, getProducts(kafkaMessage));
        } catch (SKTException ex) {
            sendErrorMessage(kafkaMessage);
        } catch (Exception ex) {
            LOG.error("#{} - There was an unexpected error in the flow: {}", kafkaMessage.getKey(), ex.getMessage());
            sendErrorMessage(kafkaMessage);
        }
    }

    public List<ProductEntity> getProducts(KafkaMessage kafkaMessage) {
        try {
            LOG.info("#{} - Get Product list from DB", kafkaMessage.getKey());
            return productRepository.findAllProducts();
        } catch (DataAccessException | JDBCConnectionException | SQLGrammarException ex) {
            LOG.error("Database Error: {}", ex.getMessage());
            throw new InfrastructureException(ex.getMessage(), ex.getCause());
        }
    }

    public void sendProducts(KafkaMessage kafkaMessage, List<ProductEntity> productEntities) {
        LOG.info("#{} - Sending Product list to topic", kafkaMessage.getKey());
        List<KafkaProduct> products = productEntitiesToKafkaProducts(productEntities);
        kafkaService.send(kafkaMessageService.buildSelectSuccessResponse(kafkaMessage.getKey(), products));
    }

    private List<KafkaProduct> productEntitiesToKafkaProducts(List<ProductEntity> productEntities) {
        return productEntities
                .stream()
                .map(this::productEntityToKafkaProduct)
                .collect(Collectors.toList());
    }

    private KafkaProduct productEntityToKafkaProduct(ProductEntity productEntity) {
        return new KafkaProduct(productEntity.getName(), productEntity.getDescription(), productEntity.getPrice());
    }

    private void sendErrorMessage(KafkaMessage kafkaMessage) {
        kafkaService.send(kafkaMessageService.buildErrorResponse(kafkaMessage.getKey(), kafkaMessage.getAction(),
                ERR_USER_MSG));
    }
}
