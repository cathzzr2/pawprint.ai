package org.abx.virturalpet.kafka;

public interface ProducerService {
    void trackServicesHealthMetric(String serviceName, double cpuUsage, double memoryUsage, String details);
}
