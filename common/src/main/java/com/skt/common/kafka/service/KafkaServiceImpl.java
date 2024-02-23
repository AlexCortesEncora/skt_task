package com.skt.common.kafka.service;

import com.skt.common.kafka.model.KafkaMessage;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

public class KafkaServiceImpl implements KafkaService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaServiceImpl.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final KafkaMessageService kafkaMessageService;

    private final String server;

    private final String groupId;

    private final String producerTopic;

    private final String consumerTopic;

    public KafkaServiceImpl(KafkaTemplate<String, String> kafkaTemplate, KafkaMessageService kafkaMessageService,
                            String server, String groupId, String producerTopic, String consumerTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaMessageService = kafkaMessageService;
        this.server = server;
        this.groupId = groupId;
        this.producerTopic = producerTopic;
        this.consumerTopic = consumerTopic;
    }

    @Override
    public void send(KafkaMessage message) {
        try {
            kafkaTemplate.send(this.producerTopic, kafkaMessageService.parsingKafkaMessageToJson(message));
        } catch (Exception e) {
            LOG.error("Error send", e);
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<KafkaMessage> receiveMessage(UUID key) {
        final Consumer<Long, String> consumer = createConsumer();
        Optional<KafkaMessage> opKafkaMessage = Optional.empty();
        final int retries = 3;
        int noRetries = 0;

        try {
            while (noRetries < retries) {
                final ConsumerRecords<Long, String> consumerRecords = consumer.poll(10000);

                if (consumerRecords.count() > 0) {
                    Iterator<ConsumerRecord<Long, String>> iterator = consumerRecords.iterator();
                    while (iterator.hasNext()) {
                        ConsumerRecord<Long, String> record = iterator.next();
                        KafkaMessage kafkaMessage = kafkaMessageService.parsingJsonToKafkaMessage(record.value());
                        if (kafkaMessage.getKey().equals(key)) {
                            opKafkaMessage = Optional.of(kafkaMessage);
                            break;
                        }
                    }
                } else {
                    noRetries++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.commitAsync();
            consumer.close();
        }

        return opKafkaMessage;
    }

    private Consumer<Long, String> createConsumer() {
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
