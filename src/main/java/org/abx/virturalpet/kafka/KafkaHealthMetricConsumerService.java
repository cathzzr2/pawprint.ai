package org.abx.virturalpet.kafka;

import org.abx.virturalpet.model.HealthMetric;
import org.abx.virturalpet.repository.HealthMetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaHealthMetricConsumerService {
    private final HealthMetricRepository healthMetricRepository;
    private static final Logger logger = LoggerFactory.getLogger(KafkaHealthMetricConsumerService.class);

    public KafkaHealthMetricConsumerService(HealthMetricRepository healthMetricRepository) {
        this.healthMetricRepository = healthMetricRepository;
    }

    @KafkaListener(topics = "topic-health-metrics", groupId = "health-monitoring-group")
    public void listen(HealthMetric healthMetric) {

        logger.info("Received health metric: {}", healthMetric);

        // Save metric to MongoDB
        healthMetricRepository.save(healthMetric);
        logger.info("Health metric saved to MongoDB: {}", healthMetric);
    }
}
