package com.example.tracking.kafka.consumer;

import com.example.tracking.model.CourierLocationUpdatedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CourierRedisConsumer {

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
    public void consume(CourierLocationUpdatedEvent event) {

        String redisKey =
                "courier:location:" + event.getCourierId();

        redisTemplate.opsForValue()
                .set(redisKey, event);

        System.out.println(
                "Saved latest location to Redis: "
                        + event.getCourierId()
        );
    }
}