package com.skt.common.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.common.exception.input.InputDataException;
import com.skt.common.exception.input.MalformedDataException;
import com.skt.common.kafka.model.KafkaAction;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.model.KafkaMessageStatus;
import com.skt.common.kafka.model.KafkaProduct;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KafkaMessageServiceImplTest {
    private KafkaMessageService kafkaMessageService;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = mock(ObjectMapper.class);
        kafkaMessageService = new KafkaMessageServiceImpl(objectMapper);
    }

    @Test
    public void testBuildSelectRequest() {
        KafkaMessage kafkaMessage = kafkaMessageService.buildSelectRequest();
        assertNotNull(kafkaMessage);
        assertNotNull(kafkaMessage.getKey());
        assertThat(kafkaMessage.getAction(), is(KafkaAction.SELECT));
    }

    @Test
    public void testSelectSuccessResponse() {
        UUID key = getKey();
        List<KafkaProduct> products = new ArrayList<>();
        products.add(new KafkaProduct("Table", "Vintage", 14.99F));

        KafkaMessage kafkaMessage = kafkaMessageService.buildSelectSuccessResponse(key, products);
        assertNotNull(kafkaMessage);
        assertThat(kafkaMessage.getKey(), is(key));
        assertThat(kafkaMessage.getAction(), is(KafkaAction.SELECT));
        assertThat(kafkaMessage.getStatus(), is(KafkaMessageStatus.SUCCESS));
        assertThat(kafkaMessage.getPayload(), is(products));
    }

    @Test
    public void testBuildErrorResponse() {
        UUID key = getKey();
        String message = "This is a test";

        KafkaMessage kafkaMessage = kafkaMessageService.buildErrorResponse(key, KafkaAction.SELECT, message);
        assertNotNull(kafkaMessage);
        assertThat(kafkaMessage.getKey(), is(key));
        assertThat(kafkaMessage.getAction(), is(KafkaAction.SELECT));
        assertThat(kafkaMessage.getStatus(), is(KafkaMessageStatus.ERROR));
        assertThat(kafkaMessage.getPayload(), is(message));
    }

    @Test
    public void testParsingKafkaMessageToJson() throws JsonProcessingException {
        String jsonExpected = "{\"status\":\"SUCCESS\",\"payload\":\"This is a test\"}";
        when(objectMapper.writeValueAsString(any(KafkaMessage.class))).thenReturn(jsonExpected);

        String json = kafkaMessageService.parsingKafkaMessageToJson(new KafkaMessage());
        assertThat(json, CoreMatchers.not(isEmptyString()));
        assertThat(json, is(jsonExpected));
    }

    @Test(expected = MalformedDataException.class)
    public void testParsingKafkaMessageToJsonShouldThrowMalformedDataException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any(KafkaMessage.class)))
                .thenThrow(new JsonProcessingException(new Throwable("Test")) {
                });
        kafkaMessageService.parsingKafkaMessageToJson(new KafkaMessage());
    }

    @Test
    public void testParsingJsonToKafkaMessage() throws IOException {
        String json = "{}";
        when(objectMapper.readValue(json, KafkaMessage.class)).thenReturn(new KafkaMessage());
        assertNotNull(kafkaMessageService.parsingJsonToKafkaMessage(json));
    }

    @Test(expected = MalformedDataException.class)
    public void testParsingJsonToKafkaMessageShouldThrowMalformedDataException() throws IOException {
        String json = "{}";
        when(objectMapper.readValue(json, KafkaMessage.class))
                .thenThrow(new JsonMappingException("Test") {
                });
        kafkaMessageService.parsingJsonToKafkaMessage(json);
    }

    @Test(expected = InputDataException.class)
    public void testParsingJsonToKafkaMessageShouldThrowInputDataException() throws IOException {
        String json = "{}";
        when(objectMapper.readValue(json, KafkaMessage.class))
                .thenThrow(new JsonProcessingException("Test") {
                });
        kafkaMessageService.parsingJsonToKafkaMessage(json);
    }

    @Test
    public void testParsingPayloadToKafkaProducts() {
        when(objectMapper.convertValue(any(Object.class), any(TypeReference.class)))
                .thenReturn(new ArrayList<>());
        assertNotNull(kafkaMessageService.parsingPayloadToKafkaProducts(new Object()));
    }

    @Test
    public void testParsingPayloadToKafkaProductsShouldReturnEmptyCollection() {
        when(objectMapper.convertValue(any(Object.class), any(TypeReference.class)))
                .thenThrow(new IllegalArgumentException("Test") {
                });
        assertTrue(kafkaMessageService.parsingPayloadToKafkaProducts(new Object()).isEmpty());
    }

    private UUID getKey() {
        return UUID.randomUUID();
    }
}