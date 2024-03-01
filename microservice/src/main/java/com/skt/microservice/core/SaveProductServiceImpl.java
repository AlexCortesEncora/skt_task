package com.skt.microservice.core;

import com.skt.common.exception.SKTException;
import com.skt.common.exception.external.InfrastructureException;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.microservice.persistence.ProductRepository;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class SaveProductServiceImpl implements SaveProductService {

    public static final String ERR_USER_MSG = "An error occurred while processing the message";

    private static final Logger LOG = LoggerFactory.getLogger(SaveProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public void processMessage(KafkaMessage kafkaMessage) {
        try {
            sendProductId(kafkaMessage, saveProduct(kafkaMessage));
        } catch (SKTException ex) {
            sendErrorMessage(kafkaMessage);
        } catch (Exception ex) {
            LOG.error("#{} - There was an unexpected error in the flow: {}", kafkaMessage.getKey(), ex.getMessage());
            sendErrorMessage(kafkaMessage);
        }
    }

    public Integer saveProduct(KafkaMessage kafkaMessage) {
        try {
            LOG.info("#{} - Save Product into DB", kafkaMessage.getKey());
            KafkaProduct product = kafkaMessageService.parsingPayloadToKafkaProduct(kafkaMessage.getPayload());
            return productRepository.save(product.getName(), product.getDescription(), product.getPrice());
        } catch (DataAccessException | JDBCConnectionException | SQLGrammarException ex) {
            LOG.error("Database Error: {}", ex.getMessage());
            throw new InfrastructureException(ex.getMessage(), ex.getCause());
        }
    }

    public void sendProductId(KafkaMessage kafkaMessage, Integer productId) {
        LOG.info("#{} - Sending ProductId to topic", kafkaMessage.getKey());
        kafkaService.send(kafkaMessageService.buildSaveSuccessResponse(kafkaMessage.getKey(), productId));
    }

    private void sendErrorMessage(KafkaMessage kafkaMessage) {
        kafkaService.send(kafkaMessageService.buildErrorResponse(kafkaMessage.getKey(), kafkaMessage.getAction(),
                ERR_USER_MSG));
    }
}
