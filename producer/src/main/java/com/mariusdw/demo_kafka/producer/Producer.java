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
    @Value("${kafka.enabled}")
    private boolean kafkaEnabled;

    @Value("${kafka.producer.topicName}")
    private String producerTopic;

    @Value("${kafka.producer.maxRandomDelayMs}")
    private int maxRandomDelayMs;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(initialDelayString = "${kafka.producer.initialDelayMs}", fixedDelayString = "${kafka.producer.intervalMs}")
    public void produce() {
        sleep(calculateRandomSleepDelayMs(maxRandomDelayMs));
        if (kafkaEnabled) {
            kafkaTemplate.send(producerTopic, LocalDateTime.now() + ": Hello World");
            log.info("Message sent");
        }
        log.info("Message not sent, kafka not enabled");
    }

    static protected int calculateRandomSleepDelayMs(int maxRandomDelayMs) {
        return (int) (Math.random() * maxRandomDelayMs);
    }

    static protected void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            log.info("Sleep interrupted");
        }
    }
}
