package org.abx.virturalpet.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.dto.JobStatus;
import org.abx.virturalpet.dto.JobType;
import org.abx.virturalpet.exception.SqsProducerException;
import org.abx.virturalpet.model.JobProgress;
import org.abx.virturalpet.model.JobResultModel;
import org.abx.virturalpet.model.PhotoJobModel;
import org.abx.virturalpet.model.PhotoModel;
import org.abx.virturalpet.repository.JobProgressRepository;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
import org.abx.virturalpet.repository.PhotoRepository;
import org.abx.virturalpet.service.PhotoGenerationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.model.Message;

@ExtendWith(MockitoExtension.class)
public class GenImageSqsMessageProcessorTest {
    @Mock
    private PhotoGenerationService photoGenerationService;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private JobProgressRepository jobRepository;

    @Mock
    private JobResultRepository resultRepository;

    @Mock
    private PhotoJobRepository photoJobRepository;

    @InjectMocks
    private GenImageSqsMessageProcessor processor;

    @Captor
    private ArgumentCaptor<JobResultModel> jobResultCaptor;

    private UUID photoId;
    private String jobId;
    private PhotoModel photoModel;
    private PhotoJobModel photoJobModel;
    private JobProgress jobProgress;

    @BeforeEach
    void before() {
        photoId = UUID.randomUUID();
        jobId = UUID.randomUUID().toString();

        photoModel = new PhotoModel();
        photoModel.setPhotoId(photoId);
        photoModel.setS3Key("s3Key");

        photoJobModel = new PhotoJobModel();
        photoJobModel.setPhotoId(photoId);
        photoJobModel.setUserId(UUID.randomUUID());

        jobProgress = new JobProgress();
        jobProgress.setJobId(UUID.fromString(jobId));
        jobProgress.setJobStatus(JobStatus.IN_PROGRESS);
    }

    @Test
    void processMessage_Success() throws Exception {
        // Arrange
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.body())
                .thenReturn("{\"photoId\":\"" + photoId.toString() + "\", \"jobId\":\"" + jobId
                        + "\", \"jobType\":\"ENHANCE\"}");

        // Initialize the ObjectMapper to parse the message body
        ObjectMapper objectMapper = new ObjectMapper();
        ImageGenSqsDto sqsDto = objectMapper.readValue(message.body(), ImageGenSqsDto.class);

        Mockito.when(photoRepository.findByPhotoId(photoId)).thenReturn(Optional.of(photoModel));
        Mockito.when(photoGenerationService.fetchPhotoFromS3(sqsDto.photoId())).thenReturn(Path.of("photoDataPath"));
        Mockito.when(photoGenerationService.callExternalApi(JobType.ENHANCE, jobId, "photoDataPath"))
                .thenReturn("apiResponse");

        Mockito.when(photoJobRepository.findByPhotoId(photoId)).thenReturn(photoJobModel);

        // Mock job progress repository to return a non-null JobProgress
        JobProgress jobProgress = new JobProgress();
        jobProgress.setJobId(UUID.fromString(jobId));
        jobProgress.setJobStatus(JobStatus.IN_PROGRESS);
        Mockito.when(jobRepository.findByJobId(UUID.fromString(jobId))).thenReturn(jobProgress);

        // Act
        processor.processMessage(message);

        // Assert
        Mockito.verify(resultRepository).save(jobResultCaptor.capture());
        JobResultModel savedJobResult = jobResultCaptor.getValue();
        Assertions.assertEquals(UUID.fromString(jobId), savedJobResult.getJobId());
        Assertions.assertEquals(photoJobModel.getUserId(), savedJobResult.getUserId());
        Assertions.assertEquals("apiResponse", savedJobResult.getS3Key());

        Mockito.verify(jobRepository).save(jobProgress);
    }

    @Test
    void processMessage_PhotoNotFound() {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.body())
                .thenReturn("{\"photoId\":\"" + photoId.toString() + "\", \"jobId\":\"" + jobId
                        + "\", \"jobType\":\"ENHANCE\"}");

        // Mock photo not found
        Mockito.when(photoRepository.findByPhotoId(photoId)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> processor.processMessage(message));
    }

    @Test
    void processMessage_JobNotFound() throws Exception {
        // Arrange
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.body())
                .thenReturn("{\"photoId\":\"" + photoId.toString() + "\", \"jobId\":\"" + jobId
                        + "\", \"jobType\":\"ENHANCE\"}");

        // Initialize the ObjectMapper to parse the message body
        ObjectMapper objectMapper = new ObjectMapper();
        ImageGenSqsDto sqsDto = objectMapper.readValue(message.body(), ImageGenSqsDto.class);

        Mockito.when(photoRepository.findByPhotoId(photoId)).thenReturn(Optional.of(photoModel));
        Mockito.when(photoGenerationService.fetchPhotoFromS3(sqsDto.photoId())).thenReturn(Path.of("photoDataPath"));
        Mockito.when(photoGenerationService.callExternalApi(JobType.ENHANCE, jobId, "photoDataPath"))
                .thenReturn("apiResponse");

        Mockito.when(photoJobRepository.findByPhotoId(photoId)).thenReturn(null);

        Assertions.assertThrows(SqsProducerException.class, () -> processor.processMessage(message));
    }

    @Test
    void updateJobStatus_Success() {

        Mockito.when(jobRepository.findByJobId(UUID.fromString(jobId))).thenReturn(jobProgress);

        processor.updateJobStatus(jobId, JobStatus.COMPLETED);

        Assertions.assertEquals(JobStatus.COMPLETED, jobProgress.getJobStatus());
        Mockito.verify(jobRepository).save(jobProgress);
    }

    @Test
    void updateJobStatus_JobNotFound() {
        Mockito.when(jobRepository.findByJobId(UUID.fromString(jobId))).thenReturn(null);

        Assertions.assertThrows(
                SqsProducerException.class, () -> processor.updateJobStatus(jobId, JobStatus.COMPLETED));
    }
}
