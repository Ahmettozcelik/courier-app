package com.example.tracking.mapper;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.model.dto.response.CourierMovementResponseDTO;
import com.example.tracking.mongo.document.CourierMovementDocument;

public class CourierMovementMapper {

    private CourierMovementMapper() {}

    // Kafka Event → Mongo Document
    public static CourierMovementDocument toDocument(CourierLocationUpdatedEvent event) {
        return CourierMovementDocument.builder()
                .courierId(event.getCourierId())
                .orderId(event.getOrderId())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .eventId(event.getEventId())
                .timestamp(event.getTimestamp())
                .build();
    }

    // Mongo Document → Response DTO
    public static CourierMovementResponseDTO toResponse(CourierMovementDocument doc) {
        return new CourierMovementResponseDTO(
                doc.getCourierId(),
                doc.getOrderId(),
                doc.getLatitude(),
                doc.getLongitude(),
                doc.getEventId(),
                doc.getTimestamp()
        );
    }
}