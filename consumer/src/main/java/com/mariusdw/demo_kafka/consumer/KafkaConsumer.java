package com.mariusdw.demo_kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {
    @KafkaListener(id = "${kafka.consumer.groupId}", topics = "${kafka.consumer.topicName}")
    public void consume(String message) {
        /*
           Use @KafkaListener(topicPartitions = {@TopicPartition(topic = "${kafka.consumer.topicName}", partitions = { "0"})})
           to directly read from the partition.
         */
        log.info("Message consumed {}", message);
    }
}
