package com.example.tracking.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "courier_movements")
public class CourierMovementDocument {

    @Id
    private String id;

    private String courierId;
    private String orderId;

    private double latitude;
    private double longitude;

    private String eventId;
    private String timestamp;

}