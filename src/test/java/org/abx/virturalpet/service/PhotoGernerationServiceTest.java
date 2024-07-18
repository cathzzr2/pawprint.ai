package org.abx.virturalpet.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.dto.PhotoGenerationDto;
import org.abx.virturalpet.model.JobProgress;
import org.abx.virturalpet.model.JobResultModel;
import org.abx.virturalpet.model.PhotoJobModel;
import org.abx.virturalpet.model.PhotoModel;
import org.abx.virturalpet.repository.JobProgressRepository;
import org.abx.virturalpet.repository.JobResultRepository;
import org.abx.virturalpet.repository.PhotoJobRepository;
import org.abx.virturalpet.repository.PhotoRepository;
import org.abx.virturalpet.sqs.ImageGenSqsProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PhotoGernerationServiceTest {

    @Mock
    private ImageGenSqsProducer imageGenSqsProducer;

    @Mock
    private JobResultRepository jobResultRepository;

    @Mock
    private PhotoJobRepository photoJobRepository;

    @Mock
    private JobProgressRepository jobProgressRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Spy
    @InjectMocks
    private PhotoGenerationService photoGenerationService;

    @Test
    public void testGenerateImg() {
        String imageData = "testImageData";
        String photoIdStr = UUID.randomUUID().toString();
        String jobType = "enhance";
        UUID photoId = UUID.fromString(photoIdStr);
        UUID userId = UUID.randomUUID();
        PhotoModel photoModel = new PhotoModel();
        photoModel.setPhotoId(photoId);
        photoModel.setUserId(userId);
        photoModel.setS3Key("test-s3-key");

        Mockito.when(photoRepository.findByPhotoId(photoId)).thenReturn(Optional.of(photoModel));

        PhotoGenerationDto result = photoGenerationService.generateImg(imageData, photoIdStr, jobType);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(imageData, result.getImageData());
        Assertions.assertEquals(photoIdStr, result.getImageId());
        Assertions.assertEquals(jobType, result.getJobType());
        Assertions.assertEquals(userId.toString(), result.getUserId());

        Mockito.verify(photoJobRepository, Mockito.times(1)).save(ArgumentMatchers.any(PhotoJobModel.class));
        Mockito.verify(jobProgressRepository, Mockito.times(1)).save(ArgumentMatchers.any(JobProgress.class));
        Mockito.verify(imageGenSqsProducer, Mockito.times(1)).sendMessage(ArgumentMatchers.any());
    }

    @Test
    public void testCheckJobStatus() {
        UUID jobId = UUID.randomUUID();
        JobProgress jobProgress = new JobProgress();
        jobProgress.setJobId(jobId);
        jobProgress.setJobStatus("completed");

        Mockito.when(jobProgressRepository.findByJobId(jobId)).thenReturn(jobProgress);

        PhotoGenerationDto result = photoGenerationService.checkJobStatus(jobId.toString());

        Assertions.assertNotNull(result);
        Assertions.assertEquals("completed", result.getStatus());
    }

    @Test
    public void testGetGenImg() throws IOException {
        UUID jobId = UUID.randomUUID();
        JobResultModel jobResultModel = new JobResultModel();
        jobResultModel.setJobId(jobId);
        jobResultModel.setS3Key("test-s3-key");

        Mockito.when(jobResultRepository.findByJobId(jobId)).thenReturn(jobResultModel);

        // mock the result form fetchPhoto formS3
        Path tempFile = Paths.get("/var/folders/s3_3997562294666883456_test-s3-key");
        Mockito.doReturn(tempFile).when(photoGenerationService).fetchPhotoFromS3("test-s3-key");

        PhotoGenerationDto result = photoGenerationService.getGenImg(jobId.toString());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(tempFile.toString(), result.getImageData());
    }
}
