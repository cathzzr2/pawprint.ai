package org.abx.virturalpet.repository;

import org.abx.virturalpet.model.JobProgress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class JobProgressRepositoryTest {
    @Autowired
    private JobProgressRepository jobProgressRepository;

    private UUID jobId1;
    private UUID jobId2;

    @BeforeEach
    public void before() {
        jobId1 = UUID.randomUUID();
        jobId2 = UUID.randomUUID();
        JobProgress job1 = JobProgress.Builder.newBuilder()
                .withJobId(jobId1)
                .withJobType("enhance")
                .withJobStatus("in progress")
                .build();
        JobProgress job2 = JobProgress.Builder.newBuilder()
                .withJobId(jobId2)
                .withJobType("enhance")
                .withJobStatus("completed")
                .build();
        jobProgressRepository.saveAll(List.of(job1, job2));
    }

    @AfterEach
    public void cleanup() {
        jobProgressRepository.deleteAll();
    }

    @Test
    public void testFindByJobId() {
        JobProgress job = jobProgressRepository.findByJobId(jobId1);
        Assertions.assertNotNull(job);
        assertThat(job.getJobStatus()).isEqualTo("in progress");
    }

    @Test
    public void testFindByJobType() {
        List<JobProgress> jobs = jobProgressRepository.findByJobType("enhance");
        Assertions.assertNotNull(jobs);
        assertThat(jobs.get(0).getJobId()).isEqualTo(jobId1);
        assertThat(jobs.get(1).getJobId()).isEqualTo(jobId2);
    }

    @Test
    public void testFindByJobStatus() {
        List<JobProgress> jobs = jobProgressRepository.findByJobStatus("completed");
        Assertions.assertNotNull(jobs);
        assertThat(jobs.get(0).getJobId()).isEqualTo(jobId2);
    }
}
