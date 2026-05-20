package com.example.tracking.kafka.consumer;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class CourierRedisConsumer {

    private final Logger log = LoggerFactory.getLogger(CourierRedisConsumer.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CourierRedisConsumer(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "courier-location-topic", groupId = "tracking-group")
    public void consume(CourierLocationUpdatedEvent event,
                        @Header(value = "traceId", required = false) String traceId) {

        MDC.put("traceId", traceId != null ? traceId : "N/A");

        try {

            log.info("🔥 EVENT RECEIVED courierId={} lat={} lon={}",
                    event.getCourierId(),
                    event.getLatitude(),
                    event.getLongitude());

            String redisKey = "courier:location:" + event.getCourierId();

            redisTemplate.opsForValue().set(
                    redisKey,
                    objectMapper.writeValueAsString(event)
            );

            log.info("💾 REDIS KV WRITTEN key={}", redisKey);

            Long geoAdded = redisTemplate.opsForGeo().add(
                    "couriers:geo",
                    new Point(event.getLongitude(), event.getLatitude()),
                    event.getCourierId()
            );

            log.info("📍 GEO ADD RESULT={}", geoAdded);

        } catch (Exception e) {
            log.error("❌ CONSUMER ERROR", e);
        } finally {
            MDC.clear();
        }
    }
}