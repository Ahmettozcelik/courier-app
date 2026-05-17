package com.example.tracking.kafka.consumer;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.util.TraceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class CourierRedisConsumer {

    private final Logger log = LoggerFactory.getLogger(CourierRedisConsumer.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public CourierRedisConsumer(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "courier-location-topic",
            groupId = "tracking-group"
    )
    public void consume(
            CourierLocationUpdatedEvent event,
            @Header(value = "traceId", required = false) String traceId
    ) {

        MDC.put("traceId", traceId);

        try {
            String redisKey = "courier:location:" + event.getCourierId();

            String json = objectMapper.writeValueAsString(event);

            redisTemplate.opsForValue().set(redisKey, json);

            log.info("[REDIS-SAVE] traceId={} courierId={} status=SUCCESS",
                    traceId,
                    event.getCourierId()
            );

        } catch (Exception e) {
            log.error("[REDIS-SAVE] FAILED traceId={} courierId={}",
                    traceId,
                    event.getCourierId(),
                    e
            );
            throw new RuntimeException(e); // Kafka retry için bilinçli bırakıyoruz
        } finally {
            MDC.clear();
        }
    }
}