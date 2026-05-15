package com.example.tracking.mapper;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.model.dto.request.CourierLocationRequest;

import java.time.Instant;
import java.util.UUID;

public class CourierEventMapper {

    private CourierEventMapper() {}

    public static CourierLocationUpdatedEvent toEvent(CourierLocationRequest request) {
        return CourierLocationUpdatedEvent.builder()
                .courierId(request.getCourierId())
                .orderId(request.getOrderId())
                .eventId(UUID.randomUUID().toString())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(Instant.now().toString())
                .build();
    }
}