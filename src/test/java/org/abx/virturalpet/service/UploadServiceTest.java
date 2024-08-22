package org.abx.virturalpet.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.abx.virturalpet.model.PhotoModel;
import org.abx.virturalpet.repository.PhotoRepository;
import org.abx.virturalpet.service.S3Service.S3UploadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

public class UploadServiceTest {

    private S3Service s3Service;
    private S3Presigner s3Presigner;
    private PhotoRepository photoRepository;
    private S3Client s3Client;
    private GenerativeAiService generativeAiService;
    private UploadService uploadService;
    private RequestBodyFactory requestBodyFactory;

    private final String bucketName = "test-bucket";

    @BeforeEach
    public void beforeEach() {
        s3Service = Mockito.mock(S3Service.class);
        s3Presigner = Mockito.mock(S3Presigner.class);
        photoRepository = Mockito.mock(PhotoRepository.class);
        s3Client = Mockito.mock(S3Client.class);
        generativeAiService = Mockito.mock(GenerativeAiService.class);

        uploadService =
                new UploadService(s3Service, s3Presigner, bucketName, photoRepository, s3Client, generativeAiService);
    }

    @Test
    public void testGeneratePresignedUrl() throws MalformedURLException {
        String objectKey = "uploads/test-file.txt";
        String expectedUrl = "https://example.com/presigned-url";

        // Mock S3Service's generatePresignedUrl
        Mockito.when(s3Service.generatePresignedUrl(bucketName, objectKey)).thenReturn(expectedUrl);

        // Call the method
        String actualUrl = uploadService.generatePresignedUrl(bucketName, objectKey);

        // Assertions
        Assertions.assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testUploadObject() throws S3UploadException, IOException {
        String fileName = "test-file.txt";
        byte[] fileData = "test data".getBytes();
        String expectedObjectKey = "uploads/" + fileName;

        // Mock the S3Service's uploadObject method
        Mockito.doNothing().when(s3Service).uploadObject(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        // Call the method
        String actualObjectKey = uploadService.uploadFile(fileName, fileData);

        // Verify that s3Service.uploadObject was called with the correct arguments
        ArgumentCaptor<String> bucketNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> objectKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> filePathCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(s3Service, Mockito.times(1))
                .uploadObject(bucketNameCaptor.capture(), objectKeyCaptor.capture(), filePathCaptor.capture());

        Assertions.assertEquals(bucketName, bucketNameCaptor.getValue());
        Assertions.assertEquals(expectedObjectKey, objectKeyCaptor.getValue());

        // Optional -  verify the temporary file handling
        File tempFile = new File(filePathCaptor.getValue());
        Assertions.assertFalse(tempFile.exists()); // Temp file should be deleted after upload
        Assertions.assertEquals(expectedObjectKey, actualObjectKey);
    }

    // A factory class to create RequestBody instances
    class RequestBodyFactory {
        public RequestBody fromFile(File file) {
            return RequestBody.fromFile(file);
        }
    }

    @Test
    public void testUploadMediaRequest() throws S3UploadException {
        String photoId = UUID.randomUUID().toString();
        String fileName = "test-file.txt";
        byte[] fileData = "test data".getBytes();

        PhotoModel photoModel = new PhotoModel();
        photoModel.setUserId(UUID.randomUUID());

        // Ensure the mocked repository returns the expected PhotoModel
        Mockito.when(photoRepository.findByPhotoId(UUID.fromString(photoId))).thenReturn(Optional.of(photoModel));

        // Mock the S3Service to do nothing on uploadObject
        Mockito.doNothing().when(s3Service).uploadObject(Mockito.anyString(), Mockito.anyString(), Mockito.any());

        // Mock the HeadObjectResponse from S3Client
        HeadObjectResponse headObjectResponse = Mockito.mock(HeadObjectResponse.class);
        Mockito.when(headObjectResponse.metadata()).thenReturn(Map.of("x-amz-meta-myVal", "test"));
        Mockito.when(s3Client.headObject(Mockito.any(HeadObjectRequest.class))).thenReturn(headObjectResponse);

        // Call the uploadMediaRequest method
        UploadServiceDto result = uploadService.uploadMediaRequest(photoId, fileName, fileData);

        // Verify the result
        Assertions.assertNotNull(result);
        Assertions.assertEquals(UploadService.MEDIA_UPLOAD_SUCCESS, result.getStatusMsg());
    }

    @Test
    public void testUploadMediaRequest_MissingData() throws S3UploadException {
        String photoId = UUID.randomUUID().toString();
        String fileName = "";
        byte[] fileData = null;

        UploadServiceDto result = uploadService.uploadMediaRequest(photoId, fileName, fileData);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(UploadService.MEDIA_NOT_PROVIDED, result.getStatusMsg());
    }
}
