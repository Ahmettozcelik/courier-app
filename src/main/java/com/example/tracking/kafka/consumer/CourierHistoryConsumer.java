package com.example.tracking.kafka.consumer;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.mongo.document.CourierMovementDocument;
import com.example.tracking.repository.CourierMovementRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CourierHistoryConsumer {

    private final CourierMovementRepository repository;

    public CourierHistoryConsumer(
            CourierMovementRepository repository
    ) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "courier-location-topic",
            groupId = "history-group"
    )
    public void consume(CourierLocationUpdatedEvent event) {

        CourierMovementDocument doc =
                CourierMovementDocument.builder()
                        .courierId(event.getCourierId())
                        .orderId(event.getOrderId())
                        .latitude(event.getLatitude())
                        .longitude(event.getLongitude())
                        .eventId(event.getEventId())
                        .timestamp(event.getTimestamp())
                        .build();

        repository.save(doc);

        System.out.println(
                "Saved history to Mongo: "
                        + event.getCourierId()
        );
    }
}