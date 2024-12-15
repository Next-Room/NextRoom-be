package com.nextroom.nextRoomServer.util.aws;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.INTERNAL_SERVER_ERROR;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.INVALID_FILE_NAME;

import com.nextroom.nextRoomServer.exceptions.CustomException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

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
        if (imageCount == 0) {
            return null;
        }

        List<String> imageUrlList = new ArrayList<>();
        for (int i = 1; i <= imageCount; i++) {
            imageUrlList.add(this.generatePresignedUrlForUpload(shopId, themeId, type));
        }
        return imageUrlList;
    }

    public String generatePresignedUrlForUpload(Long shopId, Long themeId, String type) {
        String fileName = this.createFileNameForUpload(shopId, themeId, type, System.nanoTime());
        return this.createPresignedPutUrl(fileName);
    }

    public List<String> generatePresignedUrlsForDownLoad(Long shopId, Long themeId, String type, List<String> uuidList) {
        if (uuidList == null || uuidList.isEmpty()) {
            return null;
        }

        List<String> imageUrlList = new ArrayList<>();
        for (String s : uuidList) {
            imageUrlList.add(this.generatePresignedUrlForDownLoad(shopId, themeId, type, s));
        }
        return imageUrlList;
    }

    public String generatePresignedUrlForDownLoad(Long shopId, Long themeId, String type, String timerImageUrl) {
        if (timerImageUrl == null || timerImageUrl.isEmpty()) {
            return null;
        }
        String fileName = this.createFileNameForDownLoad(shopId, themeId, type, timerImageUrl);
        return this.createPresignedGetUrl(fileName);
    }

    public void deleteObjects(Long shopId, Long themeId, String type, List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            return;
        }

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
        this.validateFileName(fileName);

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

    private String createFileNameForUpload(Long shopId, Long themeId, String type, long timestamp) {
        return String.format("%s/%s/%s/%s/%s_%s%s", profile, shopId, themeId, type, timestamp, UUID.randomUUID(), EXTENSION);
    }

    private String createFileNameForDownLoad(Long shopId, Long themeId, String type, String uuid) {
        return String.format("%s/%s/%s/%s/%s%s", profile, shopId, themeId, type, uuid, EXTENSION);
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new CustomException(INVALID_FILE_NAME);
        }
    }
}
