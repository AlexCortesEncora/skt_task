package com.skt.common.kafka.service;

import com.skt.common.exception.external.InfrastructureException;
import com.skt.common.kafka.model.KafkaMessage;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class KafkaServiceImplTest {

    private static final String SERVER = "server";
    private static final String GROUP_ID = "groupId";
    private static final String PRODUCER_TOPIC = "producerTopic";
    private static final String CONSUMER_TOPIC = "consumerTopic";
    private static final Long CONSUMER_POLL_INTERVAL = 1000L;

    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaMessageService kafkaMessageService;

    private KafkaService kafkaService;

    @Before
    public void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        kafkaMessageService = mock(KafkaMessageService.class);
        kafkaService = new KafkaServiceImpl(kafkaTemplate, kafkaMessageService, SERVER, GROUP_ID,
                PRODUCER_TOPIC, CONSUMER_TOPIC, CONSUMER_POLL_INTERVAL);
    }

    @Test
    public void testSend() throws ExecutionException, InterruptedException, TimeoutException {
        String json = "{}";
        ListenableFuture<SendResult<String, String>> future = mock(ListenableFuture.class);
        SendResult<String, String> sendResult = mock(SendResult.class);

        when(kafkaMessageService.parsingKafkaMessageToJson(any(KafkaMessage.class))).thenReturn(json);
        when(kafkaTemplate.send(PRODUCER_TOPIC, json)).thenReturn(future);
        when(future.get(2, TimeUnit.SECONDS)).thenReturn(sendResult);

        kafkaService.send(new KafkaMessage());
        verify(kafkaTemplate, times(1)).send(PRODUCER_TOPIC, json);
    }

    @Test(expected = InfrastructureException.class)
    public void testSendShouldThrowInfrastructureException() throws ExecutionException, InterruptedException, TimeoutException {
        String json = "{}";
        ListenableFuture<SendResult<String, String>> future = mock(ListenableFuture.class);

        when(kafkaMessageService.parsingKafkaMessageToJson(any(KafkaMessage.class))).thenReturn(json);
        when(kafkaTemplate.send(PRODUCER_TOPIC, json)).thenReturn(future);
        when(future.get(2, TimeUnit.SECONDS)).thenThrow(new TimeoutException());

        kafkaService.send(new KafkaMessage());
    }

    @Test
    public void testReceiveMessage() {
        UUID key = getKey();
        String json = buildKafkaMessageJson(key);
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);

        Consumer<Long, String> consumer = createConsumer(Optional.of(buildConsumerRecord(json)));

        when(kafkaMessageService.parsingJsonToKafkaMessage(json)).thenReturn(kafkaMessage);
        KafkaService kafkaService = new KafkaServiceImpl(kafkaTemplate, kafkaMessageService, SERVER, GROUP_ID,
                PRODUCER_TOPIC, CONSUMER_TOPIC, CONSUMER_POLL_INTERVAL) {
            protected Consumer<Long, String> createConsumer() {
                return consumer;
            }
        };
        Optional<KafkaMessage> opKafkaMessage = kafkaService.receiveMessage(key);
        assertTrue(opKafkaMessage.isPresent());
        assertThat(opKafkaMessage.get().getKey(), is(key));
    }

    @Test
    public void testReceiveMessageShouldReturnEmptyOptionalWhenMessageKeyIsNotFound() {
        UUID key = getKey();
        UUID otherKey = getKey();
        String json = buildKafkaMessageJson(key);
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setKey(key);

        Consumer<Long, String> consumer = createConsumer(Optional.of(buildConsumerRecord(json)));

        when(kafkaMessageService.parsingJsonToKafkaMessage(json)).thenReturn(kafkaMessage);
        KafkaService kafkaService = new KafkaServiceImpl(kafkaTemplate, kafkaMessageService, SERVER, GROUP_ID,
                PRODUCER_TOPIC, CONSUMER_TOPIC, CONSUMER_POLL_INTERVAL) {
            protected Consumer<Long, String> createConsumer() {
                return consumer;
            }
        };
        Optional<KafkaMessage> opKafkaMessage = kafkaService.receiveMessage(otherKey);
        assertFalse(opKafkaMessage.isPresent());
    }

    @Test
    public void testReceiveMessageShouldReturnEmptyOptionalWhenConsumerThrowAnIllegalStateException() {
        Consumer<Long, String> consumer = mock(Consumer.class);

        when(consumer.poll(CONSUMER_POLL_INTERVAL)).thenThrow(new IllegalStateException());
        KafkaService kafkaService = new KafkaServiceImpl(kafkaTemplate, kafkaMessageService, SERVER, GROUP_ID,
                PRODUCER_TOPIC, CONSUMER_TOPIC, CONSUMER_POLL_INTERVAL) {
            protected Consumer<Long, String> createConsumer() {
                return consumer;
            }
        };
        Optional<KafkaMessage> opKafkaMessage = kafkaService.receiveMessage(getKey());
        assertFalse(opKafkaMessage.isPresent());
    }

    private UUID getKey() {
        return UUID.randomUUID();
    }

    private String buildKafkaMessageJson(UUID key) {
        return "{\"key:\"" + key + "}";
    }

    private ConsumerRecord<Long, String> buildConsumerRecord(String json) {
        return new ConsumerRecord<>(CONSUMER_TOPIC, 0, 0, null, json);
    }

    private Consumer<Long, String> createConsumer(Optional<ConsumerRecord<Long, String>> opRecord) {
        final TopicPartition topicPartition = new TopicPartition(CONSUMER_TOPIC, 0);
        final MockConsumer<Long, String> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        final Map<TopicPartition, Long> beginningOffsets = new HashMap<>();
        mockConsumer.assign(Collections.singletonList(topicPartition));
        beginningOffsets.put(topicPartition, 0L);
        mockConsumer.schedulePollTask(() -> {
            mockConsumer.updateBeginningOffsets(beginningOffsets);
            opRecord.ifPresent(mockConsumer::addRecord);
        });

        return mockConsumer;
    }
}