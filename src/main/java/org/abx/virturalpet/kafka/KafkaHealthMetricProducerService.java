package org.abx.virturalpet.kafka;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import org.abx.virturalpet.model.HealthMetric;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Scheduled(fixedRate = 10000)
    public void collectAndSendHealthMetrics() {
        collectAndSendHealthMetrics("ExampleService");
    }

    public void collectAndSendHealthMetrics(String serviceName) {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = osBean.getSystemLoadAverage();
        double memoryUsage = (double) (Runtime.getRuntime().totalMemory()
                        - Runtime.getRuntime().freeMemory())
                / Runtime.getRuntime().totalMemory();

        String details;
        if (cpuUsage > 80) {
            details = "High CPU usage detected";
        } else if (memoryUsage > 0.9) {
            details = "High memory usage detected";
        } else {
            details = "Service running smoothly";
        }

        trackServicesHealthMetric(serviceName, cpuUsage, memoryUsage, details);
    }
}
