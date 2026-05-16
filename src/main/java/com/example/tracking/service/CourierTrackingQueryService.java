package com.example.tracking.service;

import com.example.tracking.exception.CourierNotFoundException;
import com.example.tracking.mapper.CourierMovementMapper;
import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.model.dto.response.CourierLocationResponseDTO;
import com.example.tracking.model.dto.response.CourierMovementResponseDTO;
import com.example.tracking.mongo.document.CourierMovementDocument;
import com.example.tracking.repository.CourierMovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourierTrackingQueryService {

    private static final Logger log =
            LoggerFactory.getLogger(CourierTrackingQueryService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final CourierMovementRepository repository;

    public CourierTrackingQueryService(RedisTemplate<String, Object> redisTemplate,
                                       CourierMovementRepository repository) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
    }

    public CourierLocationResponseDTO getLatestLocation(String courierId) {

        String key = "courier:location:" + courierId;

        log.info("🔍 Fetching latest location from Redis | courierId={} | key={}",
                courierId, key);

        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            log.info("Redis MISS | courierId={}", courierId);
            throw new CourierNotFoundException(courierId);
        }

        CourierLocationUpdatedEvent event =
                (CourierLocationUpdatedEvent) value;

        log.info("📦 Redis hit successful | courierId={} | lat={} | lon={}",
                event.getCourierId(), event.getLatitude(), event.getLongitude());

        return new CourierLocationResponseDTO(
                event.getCourierId(),
                event.getLatitude(),
                event.getLongitude(),
                event.getTimestamp()
        );
    }

    public List<CourierMovementResponseDTO> getHistory(String courierId) {

        log.info("📜 Fetching courier history from MongoDB | courierId={}",
                courierId);

        List<CourierMovementDocument> history =
                repository.findByCourierId(courierId);

        log.info("📊 History fetched | courierId={} | records={}",
                courierId, history.size());

        return history
                .stream()
                .map(CourierMovementMapper::toResponse)
                .toList();
    }
}