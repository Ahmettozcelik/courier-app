package com.example.tracking.repository;

import com.example.tracking.mongo.document.CourierMovementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CourierMovementRepository extends MongoRepository<CourierMovementDocument, String> {

    List<CourierMovementDocument> findByCourierId(String courierId);
}