package com.example.tracking.kafka.producer;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CourierEventProducer {
    private Logger log = LoggerFactory.getLogger(CourierEventProducer.class);

    private static final String TOPIC = "courier-location-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CourierEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLocationUpdate(CourierLocationUpdatedEvent event) {
        log.info("🚀 Sending event to Kafka | courierId={} | orderId={}",
                event.getCourierId(), event.getOrderId());

        kafkaTemplate.send(TOPIC, event.getCourierId(), event);

        log.info("✅ Event sent to topic={}", TOPIC);
    }
}