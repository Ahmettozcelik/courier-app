package com.example.tracking.service;

import com.example.tracking.exception.CourierNotFoundException;
import com.example.tracking.mapper.CourierMovementMapper;
import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.model.dto.response.CourierLocationResponseDTO;
import com.example.tracking.model.dto.response.CourierMovementResponseDTO;
import com.example.tracking.model.dto.response.CourierNearestResponse;
import com.example.tracking.mongo.document.CourierMovementDocument;
import com.example.tracking.repository.CourierMovementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourierTrackingQueryService {

    private static final Logger log = LoggerFactory.getLogger(CourierTrackingQueryService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final CourierMovementRepository repository;
    private final ObjectMapper objectMapper;

    public CourierTrackingQueryService(RedisTemplate<String, Object> redisTemplate,
                                       StringRedisTemplate stringRedisTemplate,
                                       CourierMovementRepository repository,
                                       ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Tüm kuryelerin son konumlarını getirir.
     * Performans Notu: 'redisTemplate.keys()' yerine 'SCAN' kullanılarak Redis'in kilitlenmesi engellenmiştir.
     */
    public List<CourierLocationResponseDTO> getAllLatestLocations() {
        List<CourierLocationResponseDTO> result = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().match("courier:location:*").count(100).build();

        try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                Object value = redisTemplate.opsForValue().get(key);
                if (value == null) continue;

                try {
                    CourierLocationUpdatedEvent event = objectMapper.readValue(value.toString(), CourierLocationUpdatedEvent.class);
                    result.add(new CourierLocationResponseDTO(
                            event.getCourierId(),
                            event.getLatitude(),
                            event.getLongitude(),
                            event.getTimestamp()
                    ));
                } catch (Exception e) {
                    log.error("❌ Redis parse error for key={}", key, e);
                }
            }
        } catch (Exception e) {
            log.error("❌ Error execution SCAN command on Redis", e);
        }

        if (result.isEmpty()) {
            log.info("ℹ️ Redis empty - no couriers found");
        }
        return result;
    }

    /**
     * Belirli bir kuryenin son konumunu getirir.
     */
    public CourierLocationResponseDTO getLatestLocation(String courierId) {
        String key = "courier:location:" + courierId;
        log.info("🔍 Fetching latest location from Redis | courierId={} | key={}", courierId, key);

        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            log.info("⚠️ Redis MISS | courierId={}", courierId);
            throw new CourierNotFoundException(courierId);
        }

        try {
            CourierLocationUpdatedEvent event = objectMapper.readValue(value.toString(), CourierLocationUpdatedEvent.class);
            log.info("📦 Redis hit successful | courierId={} | lat={} | lon={}", event.getCourierId(), event.getLatitude(), event.getLongitude());

            return new CourierLocationResponseDTO(
                    event.getCourierId(),
                    event.getLatitude(),
                    event.getLongitude(),
                    event.getTimestamp()
            );
        } catch (Exception e) {
            log.error("❌ Failed to parse Redis value to CourierLocationUpdatedEvent", e);
            throw new RuntimeException("Redis data parsing error", e);
        }
    }

    /**
     * Kuryenin MongoDB üzerindeki tüm geçmiş hareket listesini döner.
     */
    public List<CourierMovementResponseDTO> getHistory(String courierId) {
        log.info("📜 Fetching courier history from MongoDB | courierId={}", courierId);
        List<CourierMovementDocument> history = repository.findByCourierId(courierId);
        log.info("📊 History fetched | courierId={} | records={}", courierId, history.size());

        return history.stream()
                .map(CourierMovementMapper::toResponse)
                .toList();
    }

    /**
     * Verilen koordinata maksimum 500 KM uzaklıktaki en yakın kuryeyi mesafe bilgisiyle döner.
     * Güncelleme: Redis 6.2+ ve Spring Boot 2.6+ uyumlu güncel 'search' metoduna geçirilmiştir.
     */
    public CourierNearestResponse getNearestCourier(double lat, double lon) {
        RedisGeoCommands.GeoSearchCommandArgs args = RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
                .includeDistance() // Mesafe bilgisini hesaplatıp DTO'ya eklemek için kritik ayar
                .sortAscending()   // En yakından en uzağa sıralar
                .limit(1);   // Sadece en yakın 1 kuryeyi getirir

        GeoResults<RedisGeoCommands.GeoLocation<String>> result = stringRedisTemplate.opsForGeo().search(
                "couriers:geo",
                GeoReference.fromCoordinate(lon, lat), // Redis parametre sırası: Longitude, Latitude
                new Distance(500, Metrics.KILOMETERS),
                args
        );

        if (result == null || result.getContent().isEmpty()) {
            log.info("ℹ️ No courier found within 500km radius for lat={} lon={}", lat, lon);
            return null;
        }

        GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult = result.getContent().get(0);
        return new CourierNearestResponse(
                geoResult.getContent().getName(),
                geoResult.getDistance().getValue()
        );
    }
}
