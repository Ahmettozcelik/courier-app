package com.example.tracking.model.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourierLocationRequest {
    private String courierId;
    private String orderId;
    private double latitude;
    private double longitude;
}