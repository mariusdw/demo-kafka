package com.mariusdw.demo_kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class Producer {
    final private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.producer.topicName}")
    private String producerTopic;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(initialDelayString = "${kafka.producer.initialDelayMs}", fixedRateString = "${kafka.producer.fixedRateMs}")
    public void produce() {
        kafkaTemplate.send(producerTopic, "Hello World: " + LocalDateTime.now());
        log.info("Message sent {}", LocalDateTime.now());
    }
}
