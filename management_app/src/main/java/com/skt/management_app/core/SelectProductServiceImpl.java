package com.skt.management_app.core;

import com.skt.common.exception.SKTException;
import com.skt.common.exception.business.BusinessException;
import com.skt.common.exception.external.ThirdPartyServiceException;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaMessageStatus;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.management_app.model.Product;
import com.skt.management_app.model.SelectProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SelectProductServiceImpl implements SelectProductService {

    private static final Logger LOG = LoggerFactory.getLogger(SelectProductServiceImpl.class);
    private static final String ERROR_USER_MSG = "Oh no! Something bad happened. Please contact with your Administrator";

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private ProductResponseBuilderImpl responseBuilder;

    @Override
    public SelectProductResponse selectAll() {
        try {
            UUID kafkaMessageKey = sendMessage();
            return responseBuilder.buildSelectProductsSuccessResponse(readResponse(kafkaMessageKey));
        } catch (SKTException ex) {
            return responseBuilder.buildSelectProductsErrorResponse(ERROR_USER_MSG);
        } catch (Exception ex) {
            LOG.error("There was an unexpected error in the flow: {}", ex.getMessage());
            return responseBuilder.buildSelectProductsErrorResponse(ERROR_USER_MSG);
        }
    }

    public UUID sendMessage() {
        KafkaMessage kafkaMessage = kafkaMessageService.buildSelectRequest();
        LOG.info("#{} - Sending Kafka Message: {}", kafkaMessage.getKey(), kafkaMessage);
        kafkaService.send(kafkaMessage);
        return kafkaMessage.getKey();
    }

    public List<Product> readResponse(UUID kafkaMessageKey) {
        List<Product> products = new ArrayList<>();
        LOG.info("#{} - Subscribe topic and wait for the response", kafkaMessageKey);
        Optional<KafkaMessage> opMessage = kafkaService.receiveMessage(kafkaMessageKey);
        if (opMessage.isPresent()) {
            opMessage.ifPresent(message -> {
                LOG.info("#{} - Parsing Message to product list", kafkaMessageKey);
                if (message.getStatus().equals(KafkaMessageStatus.SUCCESS)) {
                    products.addAll(kafkaProductsToProducts(message));
                } else {
                    LOG.error("Message with error status: {}", message.getPayload());
                    throw new ThirdPartyServiceException("Message with error status: " + message.getPayload());
                }
            });
        } else {
            LOG.warn("Response was never received for the key: {}", kafkaMessageKey);
            throw new BusinessException("Response was never received for the key: " + kafkaMessageKey);
        }
        LOG.info("#{} - Getting {} products from DB", kafkaMessageKey, products.size());
        return products;
    }

    protected List<Product> kafkaProductsToProducts(KafkaMessage message) {
        return (kafkaMessageService.parsingPayloadToKafkaProducts(message.getPayload()))
                .stream()
                .map(this::kafkaProductToProduct)
                .collect(Collectors.toList());
    }

    private Product kafkaProductToProduct(KafkaProduct kafkaProduct) {
        return new Product(kafkaProduct.getName(), kafkaProduct.getDescription(), kafkaProduct.getPrice());
    }
}
