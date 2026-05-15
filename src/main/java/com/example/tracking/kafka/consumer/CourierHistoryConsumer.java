package com.example.tracking.kafka.consumer;

import com.example.tracking.mapper.CourierMovementMapper;
import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.mongo.document.CourierMovementDocument;
import com.example.tracking.repository.CourierMovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CourierHistoryConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(CourierHistoryConsumer.class);

    private final CourierMovementRepository repository;

    public CourierHistoryConsumer(CourierMovementRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "courier-location-topic",
            groupId = "history-group"
    )
    public void consume(CourierLocationUpdatedEvent event) {

        log.info("📥 Kafka event received for history | courierId={} | eventId={}",
                event.getCourierId(), event.getEventId());

        CourierMovementDocument doc =
                CourierMovementMapper.toDocument(event);

        repository.save(doc);

        log.info("💾 Mongo history saved | courierId={} | eventId={}",
                event.getCourierId(), event.getEventId());
    }
}