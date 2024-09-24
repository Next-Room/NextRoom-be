package com.nextroom.nextRoomServer.util.aws;

import com.nextroom.nextRoomServer.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class S3Component {
    @Value("${spring.config.activate.on-profile}")
    private String profile;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner presigner;

    private final static String EXTENSION = ".png";

    public List<String> generatePresignedUrlsForUpload(Long shopId, Long themeId, String type, int imageCount) {
        if (imageCount == 0) return null;

        List<String> imageUrlList = new ArrayList<>();
        for (int i = 1; i <= imageCount; i++) {
            String fileName = this.createFileNameForUpload(shopId, themeId, type, i);
            imageUrlList.add(this.createPresignedPutUrl(fileName));
        }
        return imageUrlList;
    }

    public List<String> generatePresignedUrlsForDownLoad(Long shopId, Long themeId, String type, List<String> uuidList) {
        if (uuidList == null || uuidList.isEmpty()) return null;

        List<String> imageUrlList = new ArrayList<>();
        for (String s : uuidList) {
            String fileName = this.createFileNameForDownLoad(shopId, themeId, type, s);
            imageUrlList.add(this.createPresignedGetUrl(fileName));
        }
        return imageUrlList;
    }

    public void deleteObjects(Long shopId, Long themeId, String type, List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) return;

        for (String s : imageList) {
            String fileName = this.createFileNameForDownLoad(shopId, themeId, type, s);
            this.deleteObject(fileName);
        }
    }

    private String createPresignedPutUrl(String fileName) {
        this.validateFileName(fileName);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("image/png")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        return presigner.presignPutObject(presignRequest).url().toExternalForm();
    }

    private String createPresignedGetUrl(String fileName) {
        this.validateFileName(fileName);

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(objectRequest)
                .build();

        return presigner.presignGetObject(presignRequest).url().toExternalForm();
    }

    private void deleteObject(String fileName) {
        if (fileName == null || fileName.isEmpty()) return;

        try {
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(objectRequest);
        } catch (S3Exception e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);
        }
    }

    private String createFileNameForUpload(Long shopId, Long themeId, String type, int i) {
        return String.format("%s/%s/%s/%s/%s_%s%s", profile, shopId, themeId, type, i, UUID.randomUUID(), EXTENSION);
    }

    private String createFileNameForDownLoad(Long shopId, Long themeId, String type, String uuid) {
        return String.format("%s/%s/%s/%s/%s%s", profile, shopId, themeId, type, uuid, EXTENSION);
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new CustomException(INTERNAL_SERVER_ERROR);
        }
    }
}
