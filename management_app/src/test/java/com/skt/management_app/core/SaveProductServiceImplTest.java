package com.skt.management_app.core;

import com.skt.common.exception.SKTException;
import com.skt.common.exception.business.BusinessException;
import com.skt.common.exception.external.ThirdPartyServiceException;
import com.skt.common.kafka.model.KafkaAction;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaMessageStatus;
import com.skt.common.kafka.model.KafkaProduct;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import com.skt.management_app.model.Product;
import com.skt.management_app.model.ResponseStatus;
import com.skt.management_app.model.SaveProductResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static com.skt.management_app.core.ProductResponseBuilderImpl.SAVE_PROD_SUCC_RS;
import static com.skt.management_app.core.SaveProductServiceImpl.ERROR_USER_MSG;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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
    private KafkaService kafkaService;

    @Mock
    private KafkaMessageService kafkaMessageService;

    @Mock
    private ProductResponseBuilderImpl responseBuilder;

    @Test
    public void Given_Save_When_ReceiveASuccessMessageFromTopic_Then_ReturnSaveProductSuccessResponse() {
        UUID key = getKey();
        Product product = getProduct();
        KafkaProduct kafkaProduct = getKafkaProduct();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key, kafkaProduct);
        KafkaMessage kafkaResMessage = getKafkaResSuccessMessage(key, 1);
        SaveProductResponse responseExpected = getSaveProductSuccessResponse(product);

        when(kafkaMessageService.buildSaveProductRequest(product.getName(), product.getDescription(), product.getPrice()))
                .thenReturn(kafkaReqMessage);
        doNothing().when(kafkaService).send(kafkaReqMessage);

        when(kafkaService.receiveMessage(key)).thenReturn(Optional.of(kafkaResMessage));
        when(responseBuilder.buildSaveProductSuccessResponse(product)).thenReturn(responseExpected);

        SaveProductResponse response = saveProductService.save(product);
        assertThat(response.getStatus(), is(ResponseStatus.SUCCESS));
        assertThat(response.getMessage(), is(SAVE_PROD_SUCC_RS));
        assertThat(response.getProduct(), is(product));
    }

    @Test
    public void Given_Save_When_SendMessageAndThrowSKTException_Then_ReturnSaveProductErrorResponse() {
        UUID key = getKey();
        Product product = getProduct();
        KafkaProduct kafkaProduct = getKafkaProduct();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key, kafkaProduct);
        SaveProductResponse responseExpected = getSaveProductErrorResponse();

        when(kafkaMessageService.buildSaveProductRequest(product.getName(), product.getDescription(), product.getPrice()))
                .thenReturn(kafkaReqMessage);
        doThrow(new SKTException("This is a test")).when(kafkaService).send(kafkaReqMessage);
        when(responseBuilder.buildSaveProductErrorResponse(ERROR_USER_MSG)).thenReturn(responseExpected);

        SaveProductResponse response = saveProductService.save(product);
        assertThat(response.getStatus(), is(ResponseStatus.ERROR));
        assertThat(response.getMessage(), is(ERROR_USER_MSG));
    }

    @Test
    public void Given_Save_When_SendMessageAndThrowException_Then_ReturnSaveProductErrorResponse() {
        UUID key = getKey();
        Product product = getProduct();
        KafkaProduct kafkaProduct = getKafkaProduct();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(key, kafkaProduct);
        SaveProductResponse responseExpected = getSaveProductErrorResponse();

        when(kafkaMessageService.buildSaveProductRequest(product.getName(), product.getDescription(), product.getPrice()))
                .thenReturn(kafkaReqMessage);
        doThrow(new NullPointerException("Unexpected Exception")).when(kafkaService).send(kafkaReqMessage);
        when(responseBuilder.buildSaveProductErrorResponse(ERROR_USER_MSG)).thenReturn(responseExpected);

        SaveProductResponse response = saveProductService.save(product);
        assertThat(response.getStatus(), is(ResponseStatus.ERROR));
        assertThat(response.getMessage(), is(ERROR_USER_MSG));
    }

    @Test
    public void Given_SendMessage_When_KafkaServiceSendMessageSuccessfully_Then_ReturnKey() {
        UUID keyExpected = getKey();
        Product product = getProduct();
        KafkaProduct kafkaProduct = getKafkaProduct();
        KafkaMessage kafkaReqMessage = getKafkaReqMessage(keyExpected, kafkaProduct);

        when(kafkaMessageService.buildSaveProductRequest(product.getName(), product.getDescription(), product.getPrice()))
                .thenReturn(kafkaReqMessage);
        doNothing().when(kafkaService).send(kafkaReqMessage);

        UUID key = saveProductService.sendMessage(product);
        assertThat(key, is(keyExpected));
    }

    @Test
    public void Given_ReadResponse_When_KafkaServiceReturnSuccessMessage_Then_ReturnProductId() {
        UUID key = getKey();
        Product productRequest = getProduct();
        KafkaMessage kafkaResMessage = getKafkaResSuccessMessage(key, PRODUCT_ID);

        when(kafkaService.receiveMessage(key)).thenReturn(Optional.of(kafkaResMessage));

        Product product = saveProductService.readResponse(key, productRequest);
        assertThat(product.getId(), is(PRODUCT_ID));
    }

    @Test(expected = ThirdPartyServiceException.class)
    public void Given_ReadResponse_When_KafkaServiceReturnErrorMessage_Then_ThrowAnThirdPartyServiceException() {
        UUID key = getKey();
        Product product = getProduct();
        KafkaMessage kafkaResMessage = getKafkaResErrorMessage(key);

        when(kafkaService.receiveMessage(key)).thenReturn(Optional.of(kafkaResMessage));
        saveProductService.readResponse(key, product);
    }

    @Test(expected = BusinessException.class)
    public void Given_ReadResponse_When_KafkaServiceReturnAnEmptyOptional_Then_ThrowABusinessException() {
        UUID key = getKey();
        Product product = getProduct();

        when(kafkaService.receiveMessage(key)).thenReturn(Optional.empty());
        saveProductService.readResponse(key, product);
    }

    private UUID getKey() {
        return UUID.randomUUID();
    }

    private Product getProduct() {
        return new Product(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE);
    }

    private KafkaProduct getKafkaProduct() {
        return new KafkaProduct(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE);
    }

    private KafkaMessage getKafkaReqMessage(UUID key, KafkaProduct product) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setAction(KafkaAction.SAVE);
        kafkaMessage.setKey(key);
        kafkaMessage.setPayload(product);
        return kafkaMessage;
    }

    private KafkaMessage getKafkaResErrorMessage(UUID key) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setAction(KafkaAction.SAVE);
        kafkaMessage.setStatus(KafkaMessageStatus.ERROR);
        return kafkaMessage;
    }

    private KafkaMessage getKafkaResSuccessMessage(UUID key, Integer productId) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setAction(KafkaAction.SAVE);
        kafkaMessage.setStatus(KafkaMessageStatus.SUCCESS);
        kafkaMessage.setPayload(productId);
        return kafkaMessage;
    }

    private SaveProductResponse getSaveProductSuccessResponse(Product product) {
        SaveProductResponse response = new SaveProductResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setMessage(SAVE_PROD_SUCC_RS);
        response.setProduct(product);
        return response;
    }

    private SaveProductResponse getSaveProductErrorResponse() {
        SaveProductResponse response = new SaveProductResponse();
        response.setStatus(ResponseStatus.ERROR);
        response.setMessage(ERROR_USER_MSG);
        return response;
    }
}