package org.abx.virturalpet.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.JobResultModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class JobResultRepositoryIntegrationTest {

    @Autowired
    private JobResultRepository jobResultRepository;

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("virtual_pet_db_test")
            .withUsername("test123")
            .withPassword("test123");

    @BeforeAll
    static void initAll() {
        postgresContainer.withInitScript("db/migration/V1719426631__job_progress_schema.sql");
        postgresContainer.start();
        if (!postgresContainer.isRunning()) {
            throw new IllegalStateException("PostgreSQL container failed to start");
        }
    }

    @AfterAll
    static void tearDownAll() {
        postgresContainer.stop();
    }

    @Test
    void testSaveAndFindByResultId() {
        UUID resultId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Timestamp generatedTime = new Timestamp(System.currentTimeMillis());
        String s3Key = "some-key";

        JobResultModel jobResult = new JobResultModel(resultId, jobId, userId, generatedTime, s3Key);
        jobResultRepository.save(jobResult);

        JobResultModel foundJobResult = jobResultRepository.findByResultId(resultId);
        Assertions.assertNotNull(foundJobResult);
        Assertions.assertEquals(resultId, foundJobResult.getResultId());
    }

    @Test
    void testFindByJobId() {
        UUID resultId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Timestamp generatedTime = new Timestamp(System.currentTimeMillis());
        String s3Key = "some-key";

        JobResultModel jobResult = new JobResultModel(resultId, jobId, userId, generatedTime, s3Key);
        jobResultRepository.save(jobResult);

        JobResultModel foundJobResult = jobResultRepository.findByJobId(jobId);
        Assertions.assertNotNull(foundJobResult);
        Assertions.assertEquals(jobId, foundJobResult.getJobId());
    }

    @Test
    void testFindByGeneratedTime() {
        UUID resultId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Timestamp generatedTime = new Timestamp(System.currentTimeMillis());
        String s3Key = "some-key";

        JobResultModel jobResult = new JobResultModel(resultId, jobId, userId, generatedTime, s3Key);
        jobResultRepository.save(jobResult);

        Optional<List<JobResultModel>> foundJobResults = jobResultRepository.findByGeneratedTime(generatedTime);
        Assertions.assertTrue(foundJobResults.isPresent());
        Assertions.assertFalse(foundJobResults.get().isEmpty());
        Assertions.assertEquals(generatedTime, foundJobResults.get().get(0).getGeneratedTime());
    }

    @Test
    void testFindByS3Key() {
        UUID resultId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Timestamp generatedTime = new Timestamp(System.currentTimeMillis());
        String s3Key = "some-key";

        JobResultModel jobResult = new JobResultModel(resultId, jobId, userId, generatedTime, s3Key);
        jobResultRepository.save(jobResult);

        Optional<List<JobResultModel>> foundJobResults = jobResultRepository.findByS3Key(s3Key);
        Assertions.assertTrue(foundJobResults.isPresent());
        Assertions.assertFalse(foundJobResults.get().isEmpty());
        Assertions.assertEquals(s3Key, foundJobResults.get().get(0).getS3Key());
    }
}
