package com.example.tracking.kafka.consumer;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
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
    public void consume(CourierLocationUpdatedEvent event) {

        log.info("📩 Event received from Kafka | courierId={} | lat={} | lon={}",
                event.getCourierId(), event.getLatitude(), event.getLongitude());

        String redisKey = "courier:location:" + event.getCourierId();

        redisTemplate.opsForValue().set(redisKey, event);

        log.info("💾 Saved to Redis | key={}", redisKey);
    }
}