package com.example.tracking.kafka.producer;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.util.TraceUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
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

        ProducerRecord<String, Object> record =
                new ProducerRecord<>(TOPIC, event.getCourierId(), event);

        record.headers().add(
                "traceId",
                TraceUtil.getTraceId().getBytes()
        );

        kafkaTemplate.send(record);

        log.info("[KAFKA-SENT] traceId={} courierId={}",
                TraceUtil.getTraceId(),
                event.getCourierId()
        );
    }
}