package com.example.tracking.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierLocationResponseDTO {

    private String courierId;
    private double latitude;
    private double longitude;
    private String lastUpdated;
}