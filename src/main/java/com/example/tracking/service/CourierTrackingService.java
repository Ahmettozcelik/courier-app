package com.example.tracking.service;

import com.example.tracking.kafka.producer.CourierEventProducer;
import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.model.dto.request.CourierLocationRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class CourierTrackingService {

    private final CourierEventProducer producer;

    public CourierTrackingService(CourierEventProducer producer) {
        this.producer = producer;
    }

    public void updateLocation(CourierLocationRequest request) {

        CourierLocationUpdatedEvent event = CourierLocationUpdatedEvent.builder()
                .courierId(request.getCourierId())
                .orderId(request.getOrderId())
                .eventId(UUID.randomUUID().toString())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(Instant.now().toString())
                .build();

        producer.sendLocationUpdate(event);
    }
}