package org.abx.virturalpet.service;

import org.abx.virturalpet.dto.ImmutableUploadServiceDto;
import org.abx.virturalpet.dto.UploadServiceDto;
import org.springframework.stereotype.Service;

@Service
public class UploadService {
    private static final String MEDIA_UPLOAD_SUCCESS = "media upload success";
    private static final String MEDIA_UPLOAD_FAILED = "media upload failed";
    private static final String MEDIA_NOT_PROVIDED = "media not provided";

    public UploadServiceDto uploadMediaRequest(String fileName, byte[] fileData) {
        if (fileName == null || fileName.isEmpty() || fileData == null || fileData.length == 0) {
            return ImmutableUploadServiceDto.builder()
                    .statusMsg(MEDIA_NOT_PROVIDED)
                    .fileName("")
                    .userId("")
                    .timestamp("")
                    .metadata("")
                    .build();
        }

        String s3Key = uploadFile(fileName, fileData);

        if (!s3Key.isEmpty()) {
            return ImmutableUploadServiceDto.builder()
                    .fileName(fileName)
                    .userId("userId")
                    .statusMsg(MEDIA_UPLOAD_SUCCESS)
                    .timestamp("timestamp")
                    .s3Key(s3Key)
                    .metadata("metadata")
                    .build();
        } else {
            return ImmutableUploadServiceDto.builder()
                    .fileName(fileName)
                    .userId("userId")
                    .statusMsg(MEDIA_UPLOAD_FAILED)
                    .timestamp("timestamp")
                    .metadata("metadata")
                    .build();
        }
    }

    private String uploadFile(String fileName, byte[] fileData) {
        // 实现文件上传到 S3 或其他存储服务的逻辑
        return "s3_bucket_key"; // 假设这里返回上传成功后的 S3 key
    }
}
