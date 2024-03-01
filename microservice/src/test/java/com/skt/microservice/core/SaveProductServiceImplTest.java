package com.skt.microservice.core;

import com.skt.common.kafka.model.KafkaAction;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaMessageStatus;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.microservice.persistence.ProductRepository;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.UUID;

import static com.skt.microservice.core.SelectProductServiceImpl.ERR_USER_MSG;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SaveProductServiceImplTest {

    private static final Integer PRODUCT_ID = 1;

    private static final String PRODUCT_NAME = "Item";
    private static final String PRODUCT_DESCRIPTION = "Test Item";
    private static final Float PRODUCT_PRICE = 14.99F;

    @InjectMocks
    private SaveProductServiceImpl saveProductService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaMessageService kafkaMessageService;

    @Mock
    private KafkaService kafkaService;

    @Test
    public void Given_ProcessMessage_When_ProductRepositoryReturnResult_Then_SendKafkaSuccessMessage() {
        UUID key = getKey();
        KafkaProduct kafkaProduct = getKafkaProduct();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key, kafkaProduct);
        KafkaMessage kafkaResMessage = getKafkaMessageSuccessRes(key);

        when(kafkaMessageService.parsingPayloadToKafkaProduct(kafkaReqMessage.getPayload())).thenReturn(kafkaProduct);
        when(productRepository.save(kafkaProduct.getName(), kafkaProduct.getDescription(), kafkaProduct.getPrice()))
                .thenReturn(PRODUCT_ID);
        when(kafkaMessageService.buildSaveSuccessResponse(key, PRODUCT_ID)).thenReturn(kafkaResMessage);
        doNothing().when(kafkaService).send(kafkaResMessage);

        saveProductService.processMessage(kafkaReqMessage);

        verify(kafkaMessageService, timeout(1)).parsingPayloadToKafkaProduct(kafkaReqMessage.getPayload());
        verify(productRepository, timeout(1)).save(kafkaProduct.getName(), kafkaProduct.getDescription(), kafkaProduct.getPrice());
        verify(kafkaMessageService, timeout(1)).buildSaveSuccessResponse(key, PRODUCT_ID);
        verify(kafkaService, timeout(1)).send(kafkaResMessage);
    }

    @Test
    public void Given_ProcessMessage_When_ProductRepositoryThrowJDBCConnectionException_Then_SendKafkaErrorMessage() {
        UUID key = getKey();
        KafkaProduct kafkaProduct = getKafkaProduct();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key, kafkaProduct);
        KafkaMessage kafkaResMessage = getKafkaMessageErrorRes(key);

        when(kafkaMessageService.parsingPayloadToKafkaProduct(kafkaReqMessage.getPayload())).thenReturn(kafkaProduct);
        when(productRepository.save(kafkaProduct.getName(), kafkaProduct.getDescription(), kafkaProduct.getPrice()))
                .thenThrow(new JDBCConnectionException("This is a test", new SQLException()));
        when(kafkaMessageService.buildErrorResponse(kafkaReqMessage.getKey(), kafkaReqMessage.getAction(), ERR_USER_MSG))
                .thenReturn(kafkaResMessage);
        doNothing().when(kafkaService).send(kafkaResMessage);

        saveProductService.processMessage(kafkaReqMessage);

        verify(kafkaMessageService, timeout(1)).parsingPayloadToKafkaProduct(kafkaReqMessage.getPayload());
        verify(productRepository, timeout(1))
                .save(kafkaProduct.getName(), kafkaProduct.getDescription(), kafkaProduct.getPrice());
        verify(kafkaMessageService, timeout(1)).buildErrorResponse(kafkaReqMessage.getKey(), kafkaReqMessage.getAction(), ERR_USER_MSG);
        verify(kafkaService, timeout(1)).send(kafkaResMessage);
    }

    @Test
    public void Given_ProcessMessage_When_ProductRepositoryThrowUnexpectedException_Then_SendKafkaErrorMessage() {
        UUID key = getKey();
        KafkaProduct kafkaProduct = getKafkaProduct();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key, kafkaProduct);
        KafkaMessage kafkaResMessage = getKafkaMessageErrorRes(key);

        when(kafkaMessageService.parsingPayloadToKafkaProduct(kafkaReqMessage.getPayload())).thenReturn(kafkaProduct);
        when(productRepository.save(kafkaProduct.getName(), kafkaProduct.getDescription(), kafkaProduct.getPrice()))
                .thenThrow(new NullPointerException());
        when(kafkaMessageService.buildErrorResponse(kafkaReqMessage.getKey(), kafkaReqMessage.getAction(), ERR_USER_MSG))
                .thenReturn(kafkaResMessage);
        doNothing().when(kafkaService).send(kafkaResMessage);

        saveProductService.processMessage(kafkaReqMessage);

        verify(kafkaMessageService, timeout(1)).parsingPayloadToKafkaProduct(kafkaReqMessage.getPayload());
        verify(productRepository, timeout(1))
                .save(kafkaProduct.getName(), kafkaProduct.getDescription(), kafkaProduct.getPrice());
        verify(kafkaMessageService, timeout(1)).buildErrorResponse(kafkaReqMessage.getKey(), kafkaReqMessage.getAction(), ERR_USER_MSG);
        verify(kafkaService, timeout(1)).send(kafkaResMessage);
    }

    private UUID getKey() {
        return UUID.randomUUID();
    }

    private KafkaProduct getKafkaProduct() {
        return new KafkaProduct(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE);
    }

    private KafkaMessage getKafkaReqMessage(UUID key, KafkaProduct kafkaProduct) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setAction(KafkaAction.SAVE);
        kafkaMessage.setPayload(kafkaProduct);
        return kafkaMessage;
    }

    private KafkaMessage getKafkaMessageSuccessRes(UUID key) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setStatus(KafkaMessageStatus.SUCCESS);
        kafkaMessage.setAction(KafkaAction.SAVE);
        kafkaMessage.setPayload(PRODUCT_ID);
        return kafkaMessage;
    }

    private KafkaMessage getKafkaMessageErrorRes(UUID key) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setStatus(KafkaMessageStatus.ERROR);
        kafkaMessage.setAction(KafkaAction.SAVE);
        kafkaMessage.setPayload(ERR_USER_MSG);
        return kafkaMessage;
    }
}