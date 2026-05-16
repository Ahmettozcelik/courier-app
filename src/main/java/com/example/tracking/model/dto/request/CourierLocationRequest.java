package com.example.tracking.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourierLocationRequest {
    @NotBlank(message = "Courier id cannot be blank")
    private String courierId;

    @NotBlank(message = "Order id cannot be blank")
    private String orderId;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;
}