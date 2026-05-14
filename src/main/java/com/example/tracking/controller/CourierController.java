package com.example.tracking.controller;

import com.example.tracking.model.dto.request.CourierLocationRequest;
import com.example.tracking.service.CourierTrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/courier")
public class CourierController {

    private final CourierTrackingService service;

    public CourierController(CourierTrackingService service) {
        this.service = service;
    }

    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(@RequestBody CourierLocationRequest request) {

        service.updateLocation(request);

        return ResponseEntity.ok("Location sent to Kafka");
    }
}