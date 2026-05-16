package com.example.tracking.controller;

import com.example.tracking.model.dto.request.CourierLocationRequest;
import com.example.tracking.service.CourierTrackingService;
import com.example.tracking.util.TraceUtil;
import jakarta.validation.Valid;
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
    public ResponseEntity<Void> update(
            @Valid @RequestBody CourierLocationRequest request
    ) {

        log.info("[CONTROLLER] traceId={} API received | courierId={} | orderId={}",
                TraceUtil.getTraceId(),
                request.getCourierId(),
                request.getOrderId()
        );

        service.updateLocation(request);

        log.info("[CONTROLLER] traceId={} Forwarded to service layer | courierId={}",
                TraceUtil.getTraceId(),
                request.getCourierId()
        );

        return ResponseEntity.ok().build();
    }
}