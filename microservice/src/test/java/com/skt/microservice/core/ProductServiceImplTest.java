package com.skt.microservice.core;

import com.skt.common.kafka.model.KafkaAction;
import com.skt.common.kafka.model.KafkaMessage;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static com.skt.common.kafka.model.KafkaAction.DEFAULT;
import static com.skt.common.kafka.model.KafkaAction.SELECT;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private KafkaMessageService kafkaMessageService;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private SelectProductService selectProductService;

    @Test
    public void Given_ProcessMessage_When_KafkaMessageOptionIsSelect_Then_SelectProductServiceIsExecute() {
        KafkaMessage kafkaMessage = getKafkaMessage(SELECT);
        doNothing().when(selectProductService).processMessage(kafkaMessage);

        productService.processMessage(kafkaMessage);
        verify(selectProductService, times(1)).processMessage(kafkaMessage);
    }

    @Test
    public void Given_ProcessMessage_When_KafkaMessageOptionIsInvalid_Then_KafkaServiceSendErrorResponse() {
        KafkaMessage kafkaMessage = getKafkaMessage(DEFAULT);
        KafkaMessage kafkaResMessage = getKafkaMessage(DEFAULT);

        when(kafkaMessageService.buildErrorResponse(kafkaMessage.getKey(), kafkaMessage.getAction(), "Unsupported Action"))
                .thenReturn(kafkaResMessage);
        doNothing().when(kafkaService).send(kafkaResMessage);

        productService.processMessage(kafkaMessage);
        verify(kafkaService, times(1)).send(kafkaResMessage);
    }

    private KafkaMessage getKafkaMessage(KafkaAction action) {
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(UUID.randomUUID());
        kafkaMessage.setAction(action);

        return kafkaMessage;
    }
}