package org.abx.virturalpet.repository;

import org.abx.virturalpet.model.HealthMetric;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthMetricRepository extends MongoRepository<HealthMetric, String> {}
