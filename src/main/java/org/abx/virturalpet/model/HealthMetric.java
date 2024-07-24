package org.abx.virturalpet.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "health_metrics")
public class HealthMetric {
    @Id
    private String id; // MongoDB ID

    private String serviceName;

    private long timestamp;

    private double cpuUsage;

    private double memoryUsage;

    private String details;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    // Builder pattern for creating instances
    public static final class Builder {

        private String id;
        private String serviceName;
        private long timestamp;
        private double cpuUsage;
        private double memoryUsage;
        private String details;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withCpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
            return this;
        }

        public Builder withMemoryUsage(double memoryUsage) {
            this.memoryUsage = memoryUsage;
            return this;
        }

        public Builder withDetails(String details) {
            this.details = details;
            return this;
        }

        public HealthMetric build() {
            HealthMetric healthMetric = new HealthMetric();
            healthMetric.setId(id);
            healthMetric.setServiceName(serviceName);
            healthMetric.setTimestamp(timestamp);
            healthMetric.setCpuUsage(cpuUsage);
            healthMetric.setMemoryUsage(memoryUsage);
            healthMetric.setDetails(details);
            return healthMetric;
        }
    }
}
