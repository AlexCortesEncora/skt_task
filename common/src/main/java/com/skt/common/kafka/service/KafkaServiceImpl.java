package com.skt.common.kafka.service;

import com.skt.common.exception.external.InfrastructureException;
import com.skt.common.kafka.model.KafkaMessage;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaServiceImpl implements KafkaService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaServiceImpl.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final KafkaMessageService kafkaMessageService;

    private final String server;

    private final String groupId;

    private final String producerTopic;

    private final String consumerTopic;

    private final long consumerPollInterval;

    public KafkaServiceImpl(KafkaTemplate<String, String> kafkaTemplate, KafkaMessageService kafkaMessageService,
                            String server, String groupId, String producerTopic, String consumerTopic,
                            long consumerPollInterval) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaMessageService = kafkaMessageService;
        this.server = server;
        this.groupId = groupId;
        this.producerTopic = producerTopic;
        this.consumerTopic = consumerTopic;
        this.consumerPollInterval = consumerPollInterval;
    }

    @Override
    public void send(KafkaMessage message) throws InfrastructureException {
        try {
            kafkaTemplate.send(this.producerTopic, kafkaMessageService.parsingKafkaMessageToJson(message))
                    .get(2, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException | InterruptedException ex) {
            LOG.error("Error tried to send the message: {}", ex.getMessage());
            throw new InfrastructureException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public Optional<KafkaMessage> receiveMessage(UUID key) {
        final Consumer<Long, String> consumer = createConsumer();
        final int retries = 3;
        int noRetries = 1;

        try {
            while (noRetries <= retries) {
                LOG.info("(Key: {} - Retry: {}) - Reading records from topic", key, noRetries);
                final ConsumerRecords<Long, String> consumerRecords = consumer.poll(consumerPollInterval);

                LOG.info("(Key: {} - Retry: {}) - topic has {} records", key, noRetries, consumerRecords.count());
                if (consumerRecords.count() > 0) {
                    Iterator<ConsumerRecord<Long, String>> iterator = consumerRecords.iterator();
                    while (iterator.hasNext()) {
                        ConsumerRecord<Long, String> record = iterator.next();
                        KafkaMessage kafkaMessage = kafkaMessageService.parsingJsonToKafkaMessage(record.value());
                        if (kafkaMessage.getKey().equals(key)) {
                            LOG.info("(Key: {} - Retry: {}) - message found", key, noRetries);
                            return Optional.of(kafkaMessage);
                        }
                    }
                } else {
                    LOG.warn("(Key: {} - Retry: {}) - No KafkaMessage.key in topic match with key", key, noRetries);
                    noRetries++;
                }
            }
        } catch (IllegalStateException ex) {
            LOG.error("Error tried to subscribe to a topic: {}", ex.getMessage());
        } finally {
            consumer.commitAsync();
            consumer.close();
        }

        return Optional.empty();
    }

    protected Consumer<Long, String> createConsumer() {
        final Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        final Consumer<Long, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(this.consumerTopic));
        return consumer;
    }
}
