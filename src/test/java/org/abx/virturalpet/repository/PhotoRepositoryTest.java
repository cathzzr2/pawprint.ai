package org.abx.virturalpet.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.abx.virturalpet.model.PhotoModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PhotoRepositoryTest {

    @Mock
    private PhotoRepository photoRepository;

    private PhotoModel photo;

    private UUID photoId;
    private UUID userId;
    private Timestamp uploadTime;
    private String metadata;
    private String s3Key;

    @BeforeEach
    public void init() {
        photoId = UUID.randomUUID();
        userId = UUID.randomUUID();
        metadata = "test_photo_metadata";
        s3Key = "test_photo_s3key";
        uploadTime = new Timestamp(System.currentTimeMillis());
        photo = new PhotoModel();
        photo.setUserId(userId);
        photo.setPhotoId(photoId);
        photo.setMetadata(metadata);
        photo.setUploadTime(uploadTime);
        photo.setS3Key(s3Key);
    }

    @Test
    public void testFindByUserId() {
        Mockito.when(photoRepository.findByUserId(ArgumentMatchers.any(UUID.class)))
                .thenReturn(List.of(photo));

        List<PhotoModel> foundPhotos = photoRepository.findByUserId(userId);

        Assertions.assertFalse(foundPhotos.isEmpty());
        Assertions.assertEquals(userId, foundPhotos.get(0).getUserId());
    }

    @Test
    public void testFindByUploadTime() {
        Mockito.when(photoRepository.findByUploadTime(ArgumentMatchers.any(Timestamp.class)))
                .thenReturn(List.of(photo));

        List<PhotoModel> foundPhotos = photoRepository.findByUploadTime(uploadTime);

        Assertions.assertFalse(foundPhotos.isEmpty());
        Assertions.assertEquals(uploadTime, foundPhotos.get(0).getUploadTime());
    }

    @Test
    public void testFindByS3Key() {
        Mockito.when(photoRepository.findByS3Key(ArgumentMatchers.anyString())).thenReturn(Optional.of(photo));

        Optional<PhotoModel> foundPhoto = photoRepository.findByS3Key(s3Key);

        Assertions.assertFalse(foundPhoto.isEmpty());
        Assertions.assertEquals(s3Key, foundPhoto.get().getS3Key());
    }
}
