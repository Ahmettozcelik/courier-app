package com.example.tracking.service;

import com.example.tracking.mapper.CourierEventMapper;
import com.example.tracking.kafka.producer.CourierEventProducer;
import com.example.tracking.model.CourierLocationUpdatedEvent;
import com.example.tracking.model.dto.request.CourierLocationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CourierTrackingService {

    private static final Logger log =
            LoggerFactory.getLogger(CourierTrackingService.class);

    private final CourierEventProducer producer;

    public CourierTrackingService(CourierEventProducer producer) {
        this.producer = producer;
    }

    public void updateLocation(CourierLocationRequest request) {

        log.info("⚙ Processing location update | courierId={} | orderId={}",
                request.getCourierId(), request.getOrderId());

        CourierLocationUpdatedEvent event =
                CourierEventMapper.toEvent(request);

        log.info("🔄 Event mapped | eventId={} | courierId={}",
                event.getEventId(), event.getCourierId());

        producer.sendLocationUpdate(event);

        log.info("🚀 Location update sent to Kafka | courierId={} | topic=courier-location-topic",
                event.getCourierId());
    }
}