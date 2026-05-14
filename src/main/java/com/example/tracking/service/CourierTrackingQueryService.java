package com.example.tracking.service;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.model.dto.response.CourierLocationResponseDTO;
import com.example.tracking.mongo.document.CourierMovementDocument;
import com.example.tracking.repository.CourierMovementRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourierTrackingQueryService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CourierMovementRepository repository;

    public CourierTrackingQueryService(RedisTemplate<String, Object> redisTemplate,
                                       CourierMovementRepository repository) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
    }

    public CourierLocationResponseDTO getLatestLocation(String courierId) {

        String key = "courier:location:" + courierId;

        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            throw new RuntimeException("Courier not found: " + courierId);
        }

        CourierLocationUpdatedEvent event = (CourierLocationUpdatedEvent) value;

        return new CourierLocationResponseDTO(
                event.getCourierId(),
                event.getLatitude(),
                event.getLongitude(),
                event.getTimestamp()
        );
    }

    public List<CourierMovementDocument> getHistory(String courierId) {
        return repository.findByCourierId(courierId);
    }
}