package org.abx.virturalpet.kafka;

import org.abx.virturalpet.model.HealthMetric;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaHealthMetricProducerService implements ProducerService {
    public static final String TOPIC_HEALTH_METRICS = "topic-health-metrics";
    private final KafkaTemplate<String, HealthMetric> kafkaTemplate;

    public KafkaHealthMetricProducerService(KafkaTemplate<String, HealthMetric> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void trackServicesHealthMetric(String serviceName, double cpuUsage, double memoryUsage, String details) {
        HealthMetric metric = HealthMetric.Builder.newBuilder()
                .withServiceName(serviceName)
                .withTimestamp(System.currentTimeMillis())
                .withCpuUsage(cpuUsage)
                .withMemoryUsage(memoryUsage)
                .withDetails(details)
                .build();

        kafkaTemplate.send(TOPIC_HEALTH_METRICS, serviceName, metric);
    }
}
