package com.example.tracking.controller;

import com.example.tracking.model.dto.request.CourierLocationRequest;
import com.example.tracking.service.CourierTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/courier")
public class CourierController {

    private static final Logger log = LoggerFactory.getLogger(CourierController.class);
    private final CourierTrackingService service;

    public CourierController(CourierTrackingService service) {
        this.service = service;
    }

    @PostMapping("/location")
    public ResponseEntity<Void> update(@RequestBody CourierLocationRequest request) {

        log.info("📍 API received request | courierId={} | orderId={}",
                request.getCourierId(), request.getOrderId());

        service.updateLocation(request);

        log.info("🔄 Forwarded to service layer");

        return ResponseEntity.ok().build();
    }
}