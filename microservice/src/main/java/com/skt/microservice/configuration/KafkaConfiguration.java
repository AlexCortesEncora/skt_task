package com.skt.microservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.common.kafka.service.KafkaMessageService;
import com.skt.common.kafka.service.KafkaMessageServiceImpl;
import com.skt.common.kafka.service.KafkaService;
import com.skt.common.kafka.service.KafkaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfiguration {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String server;

    @Value(value = "${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value(value = "${kafka.topic.producer.name}")
    private String producerTopic;

    @Value(value = "${kafka.topic.consumer.name}")
    private String consumerTopic;

    @Value(value = "${kafka.topic.consumer.poll-interval}")
    private long consumerPollInterval;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public KafkaMessageService kafkaMessageService(ObjectMapper objectMapper) {
        return new KafkaMessageServiceImpl(objectMapper);
    }

    @Bean
    public KafkaService kafkaService(KafkaMessageService kafkaMessageService) {
        return new KafkaServiceImpl(kafkaTemplate, kafkaMessageService, server, groupId, producerTopic, consumerTopic,
                consumerPollInterval);
    }

}
