package org.abx.virturalpet.service;

import java.sql.Timestamp;
import java.util.UUID;
import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.dto.ImprovePhotoJbDto;
import org.abx.virturalpet.dto.ImprovedPhotoResultDto;
import org.abx.virturalpet.dto.JobType;
import org.abx.virturalpet.model.JobProgress;
import org.abx.virturalpet.model.JobResultModel;
import org.abx.virturalpet.model.PhotoJobModel;
import org.abx.virturalpet.repository.JobProgressRepository;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
import org.abx.virturalpet.sqs.ImageGenSqsProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class MediaQualityServiceTest {

    private PhotoJobRepository photoJobRepository;
    private JobResultRepository jobResultRepository;
    private ImageGenSqsProducer imageGenSqsProducer;
    private JobProgressRepository jobProgressRepository;

    private MediaQualityService mediaQualityService;

    @BeforeEach
    public void beforeEach() {
        photoJobRepository = Mockito.mock(PhotoJobRepository.class);
        jobResultRepository = Mockito.mock(JobResultRepository.class);
        imageGenSqsProducer = Mockito.mock(ImageGenSqsProducer.class);
        jobProgressRepository = Mockito.mock(JobProgressRepository.class);

        mediaQualityService = new MediaQualityService(
                photoJobRepository, jobResultRepository, imageGenSqsProducer, jobProgressRepository);
    }

    @Test
    public void testEnqueuePhoto() {
        UUID userId = UUID.randomUUID();
        UUID photoId = UUID.randomUUID();
        JobType jobType = JobType.ENHANCE;

        ArgumentCaptor<PhotoJobModel> photoJobCaptor = ArgumentCaptor.forClass(PhotoJobModel.class);
        ArgumentCaptor<JobProgress> jobProgressCaptor = ArgumentCaptor.forClass(JobProgress.class);
        ArgumentCaptor<ImageGenSqsDto> imageGenSqsDtoCaptor = ArgumentCaptor.forClass(ImageGenSqsDto.class);

        ImprovePhotoJbDto result = mediaQualityService.enqueuePhoto(userId, photoId, jobType);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId, result.getUserId());
        Assertions.assertEquals(photoId, result.getPhotoId());
        Assertions.assertEquals(jobType, result.getJobType());

        Mockito.verify(photoJobRepository, Mockito.times(1)).save(photoJobCaptor.capture());
        Mockito.verify(jobProgressRepository, Mockito.times(1)).save(jobProgressCaptor.capture());
        Mockito.verify(imageGenSqsProducer, Mockito.times(1)).sendMessage(imageGenSqsDtoCaptor.capture());

        PhotoJobModel capturedPhotoJob = photoJobCaptor.getValue();
        Assertions.assertNotNull(capturedPhotoJob);
        Assertions.assertEquals(photoId, capturedPhotoJob.getPhotoId());
        Assertions.assertEquals(userId, capturedPhotoJob.getUserId());
        Assertions.assertEquals(jobType.name(), capturedPhotoJob.getJobType());

        JobProgress capturedJobProgress = jobProgressCaptor.getValue();
        Assertions.assertNotNull(capturedJobProgress);
        Assertions.assertEquals(jobType.name(), capturedJobProgress.getJobType());

        ImageGenSqsDto capturedImageGenSqsDto = imageGenSqsDtoCaptor.getValue();
        Assertions.assertNotNull(capturedImageGenSqsDto);
        Assertions.assertEquals(photoId.toString(), capturedImageGenSqsDto.photoId());
    }

    @Test
    public void testGetImprovedPhoto() {
        UUID jobId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID resultId = UUID.randomUUID();
        String s3Key = "s3-key";
        Timestamp generatedTime = new Timestamp(System.currentTimeMillis());

        // set up JobResultModel
        JobResultModel jobResultModel = new JobResultModel();
        jobResultModel.setJobId(jobId);
        jobResultModel.setUserId(userId);
        jobResultModel.setResultId(resultId);
        jobResultModel.setS3Key(s3Key);
        jobResultModel.setGeneratedTime(generatedTime);

        Mockito.when(jobResultRepository.findByJobId(jobId)).thenReturn(jobResultModel);

        ImprovedPhotoResultDto result = mediaQualityService.getImprovedPhoto(jobId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(jobId, result.getJobId());
        Assertions.assertEquals(userId, result.getUserId());
        Assertions.assertEquals(resultId, result.getResultId());
        Assertions.assertEquals(s3Key, result.getS3Key());
        Assertions.assertEquals(generatedTime, result.getGeneratedTime());

        Mockito.verify(jobResultRepository, Mockito.times(1)).findByJobId(jobId);
    }

    @Test
    public void testGetImprovedPhoto_NotFound() {
        UUID jobId = UUID.randomUUID();

        Mockito.when(jobResultRepository.findByJobId(jobId)).thenReturn(null);

        RuntimeException exception =
                Assertions.assertThrows(RuntimeException.class, () -> mediaQualityService.getImprovedPhoto(jobId));

        Assertions.assertEquals("Job result not found for id: " + jobId, exception.getMessage());

        Mockito.verify(jobResultRepository, Mockito.times(1)).findByJobId(jobId);
    }
}
