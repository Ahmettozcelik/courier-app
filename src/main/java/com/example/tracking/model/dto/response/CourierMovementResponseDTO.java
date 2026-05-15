package com.example.tracking.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierMovementResponseDTO {

    private String courierId;
    private String orderId;
    private double latitude;
    private double longitude;
    private String eventId;
    private String timestamp;
}
