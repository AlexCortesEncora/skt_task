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
import com.skt.management_app.model.ResponseStatus;
import com.skt.management_app.model.SelectProductResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.skt.management_app.core.SelectProductServiceImpl.ERROR_USER_MSG;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectProductServiceImplTest {

    private static final String PRODUCT_NAME = "Item";
    private static final String PRODUCT_DESCRIPTION = "Test Item";
    private static final Float PRODUCT_PRICE = 14.99F;
    @InjectMocks
    private SelectProductServiceImpl selectProductService;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private KafkaMessageService kafkaMessageService;

    @Mock
    private ProductResponseBuilderImpl responseBuilder;

    @Test
    public void Given_SelectAll_When_ReceiveASuccessMessageFromTopic_Then_ReturnSelectProductSuccessResponse() {
        UUID key = getKey();
        KafkaMessage kafkaReqMessage = getKafkaResMessage(key);
        List<KafkaProduct> kafkaProducts = getKafkaProducts();
        KafkaMessage kafkaResMessage = getKafkaResMessage(key, KafkaMessageStatus.SUCCESS, kafkaProducts);
        List<Product> products = getProducts();
        SelectProductResponse responseExpected = getSelectProductSuccessResponse(products);

        when(kafkaMessageService.buildSelectRequest()).thenReturn(kafkaReqMessage);
        doNothing().when(kafkaService).send(kafkaReqMessage);

        when(kafkaService.receiveMessage(key)).thenReturn(Optional.of(kafkaResMessage));
        when(kafkaMessageService.parsingPayloadToKafkaProducts(kafkaResMessage.getPayload())).thenReturn(kafkaProducts);
        when(responseBuilder.buildSelectProductsSuccessResponse(any(List.class))).thenReturn(responseExpected);

        SelectProductResponse response = selectProductService.selectAll();
        assertThat(response.getStatus(), is(ResponseStatus.SUCCESS));
        assertThat(response.getProducts(), is(products));
    }

    @Test
    public void Given_SelectAll_When_SendMessageAndThrowSKTException_Then_ReturnSelectProductErrorResponse() {
        UUID key = getKey();
        KafkaMessage kafkaMessage = getKafkaResMessage(key);
        SelectProductResponse responseExpected = getSelectProductErrorResponse();

        when(kafkaMessageService.buildSelectRequest()).thenReturn(kafkaMessage);
        doThrow(new SKTException("This is a test")).when(kafkaService).send(kafkaMessage);
        when(responseBuilder.buildSelectProductsErrorResponse(ERROR_USER_MSG)).thenReturn(responseExpected);

        SelectProductResponse response = selectProductService.selectAll();
        assertThat(response.getStatus(), is(ResponseStatus.ERROR));
        assertThat(response.getMessage(), is(ERROR_USER_MSG));
        assertTrue(response.getProducts().isEmpty());
    }

    @Test
    public void Given_SelectAll_When_SendMessageAndThrowException_Then_ReturnSelectProductErrorResponse() {
        UUID key = getKey();
        KafkaMessage kafkaMessage = getKafkaResMessage(key);
        SelectProductResponse responseExpected = getSelectProductErrorResponse();

        when(kafkaMessageService.buildSelectRequest()).thenReturn(kafkaMessage);
        doThrow(new NullPointerException("Unexpected Exception")).when(kafkaService).send(kafkaMessage);
        when(responseBuilder.buildSelectProductsErrorResponse(ERROR_USER_MSG)).thenReturn(responseExpected);

        SelectProductResponse response = selectProductService.selectAll();
        assertThat(response.getStatus(), is(ResponseStatus.ERROR));
        assertThat(response.getMessage(), is(ERROR_USER_MSG));
        assertTrue(response.getProducts().isEmpty());
    }

    @Test
    public void Given_SendMessage_When_KafkaServiceSendMessageSuccessfully_Then_ReturnKey() {
        UUID keyExpected = getKey();
        KafkaMessage kafkaMessage = getKafkaResMessage(keyExpected);

        when(kafkaMessageService.buildSelectRequest()).thenReturn(kafkaMessage);
        doNothing().when(kafkaService).send(kafkaMessage);

        UUID key = selectProductService.sendMessage();
        assertThat(key, is(keyExpected));
    }

    @Test
    public void Given_ReadResponse_When_KafkaServiceReturnSuccessMessage_Then_ReturnProductList() {
        UUID key = getKey();
        List<KafkaProduct> kafkaProducts = getKafkaProducts();
        KafkaMessage kafkaResMessage = getKafkaResMessage(key, KafkaMessageStatus.SUCCESS, kafkaProducts);
        List<Product> productsExpected = getProducts();

        when(kafkaService.receiveMessage(key)).thenReturn(Optional.of(kafkaResMessage));
        when(kafkaMessageService.parsingPayloadToKafkaProducts(kafkaResMessage.getPayload())).thenReturn(kafkaProducts);

        List<Product> products = selectProductService.readResponse(key);
        assertThat(products, is(productsExpected));
    }

    @Test(expected = ThirdPartyServiceException.class)
    public void Given_ReadResponse_When_KafkaServiceReturnErrorMessage_Then_ThrowAnThirdPartyServiceException() {
        UUID key = getKey();
        KafkaMessage kafkaResMessage = getKafkaResMessage(key, KafkaMessageStatus.ERROR);

        when(kafkaService.receiveMessage(key)).thenReturn(Optional.of(kafkaResMessage));
        selectProductService.readResponse(key);
    }

    @Test(expected = BusinessException.class)
    public void Given_ReadResponse_When_KafkaServiceReturnAnEmptyOptional_Then_ThrowABusinessException() {
        UUID key = getKey();

        when(kafkaService.receiveMessage(key)).thenReturn(Optional.empty());
        selectProductService.readResponse(key);
    }

    private UUID getKey() {
        return UUID.randomUUID();
    }

    private List<KafkaProduct> getKafkaProducts() {
        return Collections.singletonList(new KafkaProduct(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE));
    }

    private List<Product> getProducts() {
        return Collections.singletonList(new Product(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE));
    }

    private KafkaMessage getKafkaResMessage(UUID key) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        return kafkaMessage;
    }

    private KafkaMessage getKafkaResMessage(UUID key, KafkaMessageStatus status) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setStatus(status);
        return kafkaMessage;
    }

    private KafkaMessage getKafkaResMessage(UUID key, KafkaMessageStatus status, Object payload) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);
        kafkaMessage.setStatus(status);
        kafkaMessage.setPayload(payload);
        return kafkaMessage;
    }

    private SelectProductResponse getSelectProductSuccessResponse(List<Product> products) {
        SelectProductResponse response = new SelectProductResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setProducts(products);
        return response;
    }

    private SelectProductResponse getSelectProductErrorResponse() {
        SelectProductResponse response = new SelectProductResponse();
        response.setStatus(ResponseStatus.ERROR);
        response.setMessage(ERROR_USER_MSG);
        response.setProducts(Collections.emptyList());
        return response;
    }
}