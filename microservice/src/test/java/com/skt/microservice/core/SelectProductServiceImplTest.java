package com.skt.microservice.core;

import com.skt.common.exception.external.InfrastructureException;
import com.skt.common.kafka.model.KafkaAction;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaMessageStatus;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.microservice.persistence.ProductRepository;
import com.skt.microservice.persistence.entity.ProductEntity;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.skt.microservice.core.SelectProductServiceImpl.ERR_USER_MSG;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectProductServiceImplTest {

    private static final String PRODUCT_NAME = "Item";
    private static final String PRODUCT_DESCRIPTION = "Test Item";
    private static final Float PRODUCT_PRICE = 14.99F;

    @InjectMocks
    private SelectProductServiceImpl selectProductService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaMessageService kafkaMessageService;

    @Mock
    private KafkaService kafkaService;

    @Test
    public void Given_ProcessMessage_When_ProductRepositoryReturnResult_Then_SendKafkaSuccessMessage() {
        UUID key = getKey();
        List<ProductEntity> productsEntity = Arrays.asList(getProductEntity());
        List<KafkaProduct> products = Arrays.asList(getKafkaProduct());

        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key);
        KafkaMessage kafkaResMessage = getKafkaMessageSuccessRes(key, products);

        when(productRepository.findAllProducts()).thenReturn(productsEntity);
        when(kafkaMessageService.buildSelectSuccessResponse(any(UUID.class), any(List.class))).thenReturn(kafkaResMessage);
        doNothing().when(kafkaService).send(kafkaResMessage);

        selectProductService.processMessage(kafkaReqMessage);

        verify(productRepository, timeout(1)).findAllProducts();
        verify(kafkaMessageService, timeout(1)).buildSelectSuccessResponse(any(UUID.class), any(List.class));
        verify(kafkaService, timeout(1)).send(kafkaResMessage);
    }

    @Test
    public void Given_ProcessMessage_When_ProductRepositoryThrowJDBCConnectionException_Then_SendKafkaErrorMessage() {
        UUID key = getKey();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key);
        KafkaMessage kafkaResMessage = getKafkaMessageErrorRes(key);

        when(productRepository.findAllProducts()).thenThrow(new JDBCConnectionException("This is a test", new SQLException()));
        when(kafkaMessageService.buildErrorResponse(kafkaReqMessage.getKey(), kafkaReqMessage.getAction(), ERR_USER_MSG))
                .thenReturn(kafkaResMessage);
        doNothing().when(kafkaService).send(kafkaResMessage);

        selectProductService.processMessage(kafkaReqMessage);

        verify(productRepository, timeout(1)).findAllProducts();
        verify(kafkaMessageService, timeout(1)).buildErrorResponse(kafkaReqMessage.getKey(), kafkaReqMessage.getAction(), ERR_USER_MSG);
        verify(kafkaService, timeout(1)).send(kafkaResMessage);
    }

    @Test
    public void Given_ProcessMessage_When_ProductRepositoryThrowUnexpectedException_Then_SendKafkaErrorMessage() {
        UUID key = getKey();
        List<ProductEntity> productsEntity = Arrays.asList(getProductEntity());

        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key);
        KafkaMessage kafkaResMessage = getKafkaMessageErrorRes(key);

        when(productRepository.findAllProducts()).thenReturn(productsEntity);
        when(kafkaMessageService.buildSelectSuccessResponse(any(UUID.class), any(List.class))).thenReturn(kafkaResMessage);
        doThrow(new NullPointerException()).when(kafkaService).send(kafkaResMessage);

        selectProductService.processMessage(kafkaReqMessage);

        verify(productRepository, timeout(1)).findAllProducts();
        verify(kafkaMessageService, timeout(1)).buildSelectSuccessResponse(any(UUID.class), any(List.class));
        verify(kafkaService, timeout(2)).send(kafkaResMessage);
    }

    @Test
    public void Given_GetProducts_When_ProductRepositoryReturnResult_Then_ReturnProductEntityList() {
        UUID key = getKey();
        List<ProductEntity> productsEntityExpected = Arrays.asList(getProductEntity());
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key);

        when(productRepository.findAllProducts()).thenReturn(productsEntityExpected);

        List<ProductEntity> productsEntity = selectProductService.getProducts(kafkaReqMessage);
        assertThat(productsEntity, is(productsEntityExpected));
    }

    @Test(expected = InfrastructureException.class)
    public void Given_GetProducts_When_ProductRepositoryThrowJDBCConnectionException_Then_ThrowInfrastructureException() {
        UUID key = getKey();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key);

        when(productRepository.findAllProducts()).thenThrow(new JDBCConnectionException("This is a test", new SQLException()));

        selectProductService.getProducts(kafkaReqMessage);
    }

    @Test
    public void Given_SendProducts_When_KafkaMessageServiceParsingProductEntityList_Then_KafkaServiceSendSuccessMessage() {
        UUID key = getKey();

        List<ProductEntity> productsEntity = Arrays.asList(getProductEntity());
        List<KafkaProduct> products = Arrays.asList(getKafkaProduct());

        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key);
        KafkaMessage kafkaResMessage = getKafkaMessageSuccessRes(key, products);

        when(kafkaMessageService.buildSelectSuccessResponse(any(UUID.class), any(List.class))).thenReturn(kafkaResMessage);
        doNothing().when(kafkaService).send(kafkaResMessage);

        selectProductService.sendProducts(kafkaReqMessage, productsEntity);

        verify(kafkaMessageService, timeout(1)).buildSelectSuccessResponse(any(UUID.class), any(List.class));
        verify(kafkaService, timeout(1)).send(kafkaResMessage);
    }

    private UUID getKey() {
        return UUID.randomUUID();
    }

    private KafkaMessage getKafkaReqMessage(UUID key) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setAction(KafkaAction.SELECT);
        return kafkaMessage;
    }

    private KafkaMessage getKafkaMessageSuccessRes(UUID key, List<KafkaProduct> products) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setStatus(KafkaMessageStatus.SUCCESS);
        kafkaMessage.setAction(KafkaAction.SELECT);
        kafkaMessage.setPayload(products);
        return kafkaMessage;
    }

    private KafkaMessage getKafkaMessageErrorRes(UUID key) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setStatus(KafkaMessageStatus.ERROR);
        kafkaMessage.setAction(KafkaAction.SELECT);
        kafkaMessage.setPayload(ERR_USER_MSG);
        return kafkaMessage;
    }

    private ProductEntity getProductEntity() {
        ProductEntity product = new ProductEntity();
        product.setName(PRODUCT_NAME);
        product.setDescription(PRODUCT_DESCRIPTION);
        product.setPrice(PRODUCT_PRICE);
        return product;
    }

    private KafkaProduct getKafkaProduct() {
        KafkaProduct product = new KafkaProduct();
        product.setName(PRODUCT_NAME);
        product.setDescription(PRODUCT_DESCRIPTION);
        product.setPrice(PRODUCT_PRICE);
        return product;
    }
}