package org.abx.virturalpet.kafka;

import java.lang.management.OperatingSystemMXBean;
import org.abx.virturalpet.model.HealthMetric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaHealthMetricProducerServiceTest {
    @Mock
    private KafkaTemplate<String, HealthMetric> kafkaTemplate;

    @InjectMocks
    private KafkaHealthMetricProducerService kafkaHealthMetricProducerService;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTrackServiceHealthMetric() {
        String serviceName = "TestService";
        double cpuUsage = 50.0;
        double memoryUsage = 0.5;
        String details = "Service running smoothly";

        kafkaHealthMetricProducerService.trackServicesHealthMetric(serviceName, cpuUsage, memoryUsage, details);

        ArgumentCaptor<HealthMetric> captor = ArgumentCaptor.forClass(HealthMetric.class);
        Mockito.verify(kafkaTemplate, Mockito.times(1))
                .send(
                        Mockito.eq(KafkaHealthMetricProducerService.TOPIC_HEALTH_METRICS),
                        Mockito.eq(serviceName),
                        captor.capture());

        HealthMetric capturedMetric = captor.getValue();
        Assertions.assertEquals(serviceName, capturedMetric.getServiceName());
        Assertions.assertEquals(cpuUsage, capturedMetric.getCpuUsage());
        Assertions.assertEquals(memoryUsage, capturedMetric.getMemoryUsage());
        Assertions.assertEquals(details, capturedMetric.getDetails());
    }

    @Test
    public void testCollectAndSendHealthMetrics() {
        String serviceName = "ExampleService";
        double totalMemory = 100L;
        double freeMemory = 60L;
        double cpuUsage = 0.5;
        double memoryUsage = (totalMemory - freeMemory) / totalMemory;

        OperatingSystemMXBean osBean = Mockito.mock(OperatingSystemMXBean.class);
        Mockito.when(osBean.getSystemLoadAverage()).thenReturn(cpuUsage);

        // mock memory usage
        String details;
        if (cpuUsage > 80) {
            details = "High CPU usage detected";
        } else if (memoryUsage > 0.9) {
            details = "High memory usage detected";
        } else {
            details = "Service running smoothly";
        }

        kafkaHealthMetricProducerService.trackServicesHealthMetric(serviceName, cpuUsage, memoryUsage, details);

        ArgumentCaptor<HealthMetric> captor = ArgumentCaptor.forClass(HealthMetric.class);
        Mockito.verify(kafkaTemplate, Mockito.times(1))
                .send(
                        Mockito.eq(KafkaHealthMetricProducerService.TOPIC_HEALTH_METRICS),
                        Mockito.eq(serviceName),
                        captor.capture());

        HealthMetric capturedMetric = captor.getValue();
        Assertions.assertEquals(serviceName, capturedMetric.getServiceName());
        Assertions.assertEquals(cpuUsage, capturedMetric.getCpuUsage());
        Assertions.assertEquals(memoryUsage, capturedMetric.getMemoryUsage());
        Assertions.assertEquals(details, capturedMetric.getDetails());
    }
}
