package com.example.tracking.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourierLocationUpdatedEvent implements Serializable {

    private String courierId;
    private String orderId;
    private String eventId;

    private double latitude;
    private double longitude;

    private String timestamp;
    
}