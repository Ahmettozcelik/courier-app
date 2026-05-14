package com.example.tracking.controller;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.model.dto.response.CourierLocationResponseDTO;
import com.example.tracking.mongo.document.CourierMovementDocument;
import com.example.tracking.service.CourierTrackingQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courier")
public class CourierQueryController {

    private final CourierTrackingQueryService queryService;

    public CourierQueryController(CourierTrackingQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{courierId}/location")
    public CourierLocationResponseDTO getLocation(@PathVariable String courierId) {
        return queryService.getLatestLocation(courierId);
    }

    @GetMapping("/history/{courierId}")
    public List<CourierMovementDocument> getHistory(@PathVariable String courierId) {
        return queryService.getHistory(courierId);
    }
}