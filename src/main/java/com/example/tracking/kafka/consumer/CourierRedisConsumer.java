package com.example.tracking.kafka.consumer;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.util.TraceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class CourierRedisConsumer {

    private Logger log = LoggerFactory.getLogger(CourierRedisConsumer.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public CourierRedisConsumer(
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(
            topics = "courier-location-topic",
            groupId = "tracking-group"
    )
    @KafkaListener(topics = "courier-location-topic", groupId = "tracking-group")
    public void consume(CourierLocationUpdatedEvent event,
                        @Header(value = "traceId", required = false) String traceId) {

        MDC.put("traceId", traceId);

        log.info("[REDIS-RECEIVE] traceId={} courierId={} key={}",
                traceId,
                event.getCourierId(),
                "courier:location:" + event.getCourierId()
        );

        String redisKey = "courier:location:" + event.getCourierId();

        redisTemplate.opsForValue().set(redisKey, event);

        log.info("[REDIS-SAVE] traceId={} courierId={} status=SUCCESS",
                traceId,
                event.getCourierId()
        );

        MDC.clear();
    }
}