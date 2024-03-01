package com.skt.management_app.core;

import com.skt.common.exception.SKTException;
import com.skt.common.exception.business.BusinessException;
import com.skt.common.exception.external.ThirdPartyServiceException;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaMessageStatus;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.management_app.model.Product;
import com.skt.management_app.model.SaveProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SaveProductServiceImpl implements SaveProductService {

    public static final String ERROR_USER_MSG = "Oh no! Something bad happened. We can't save your product. Please contact with your Administrator";

    private static final Logger LOG = LoggerFactory.getLogger(SaveProductServiceImpl.class);

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private ProductResponseBuilderImpl responseBuilder;

    @Override
    public SaveProductResponse save(Product product) {
        try {
            UUID kafkaMessageKey = sendMessage(product);
            return responseBuilder.buildSaveProductSuccessResponse(readResponse(kafkaMessageKey, product));
        } catch (SKTException ex) {
            return responseBuilder.buildSaveProductErrorResponse(ERROR_USER_MSG);
        } catch (Exception ex) {
            LOG.error("There was an unexpected error in the flow: {}", ex.getMessage());
            return responseBuilder.buildSaveProductErrorResponse(ERROR_USER_MSG);
        }
    }

    public UUID sendMessage(Product product) {
        KafkaMessage kafkaMessage = kafkaMessageService.buildSaveProductRequest(product.getName(),
                product.getDescription(), product.getPrice());
        LOG.info("#{} - Sending Kafka Message: {}", kafkaMessage.getKey(), kafkaMessage);
        kafkaService.send(kafkaMessage);
        return kafkaMessage.getKey();
    }

    public Product readResponse(UUID kafkaMessageKey, Product product) {
        LOG.info("#{} - Subscribe topic and wait for the response", kafkaMessageKey);
        Optional<KafkaMessage> opMessage = kafkaService.receiveMessage(kafkaMessageKey);
        if (opMessage.isPresent()) {
            opMessage.ifPresent(message -> {
                LOG.info("#{} - Parsing Message to product", kafkaMessageKey);
                if (message.getStatus().equals(KafkaMessageStatus.SUCCESS)) {
                    product.setId((Integer) message.getPayload());
                } else {
                    LOG.error("Message with error status: {}", message.getPayload());
                    throw new ThirdPartyServiceException("Message with error status: " + message.getPayload());
                }
            });
        } else {
            LOG.warn("Response was never received for the key: {}", kafkaMessageKey);
            throw new BusinessException("Response was never received for the key: " + kafkaMessageKey);
        }
        LOG.info("#{} - Products saved", kafkaMessageKey);
        return product;
    }
}
