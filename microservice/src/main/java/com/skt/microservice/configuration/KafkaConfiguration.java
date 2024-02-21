package com.skt.microservice.configuration;

import com.skt.common.kafka.service.KafkaService;
import com.skt.common.kafka.service.KafkaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfiguration {

    @Value(value = "${kafka.topic.response.name}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public KafkaService kafkaService() {
        return new KafkaServiceImpl(kafkaTemplate, topic);
    }
}
