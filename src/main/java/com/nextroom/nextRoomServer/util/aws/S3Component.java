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

    public String createPresignedUrl(String fileName) {
        validateFileName(fileName);

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

    public String createPresignedGetUrl(String fileName) {
        validateFileName(fileName);

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

    public void deleteObject(String fileName) {
        validateFileName(fileName);

        try {
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(objectRequest);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            throw new CustomException(INTERNAL_SERVER_ERROR);
        }
    }

    public String createFileName(Long shopId, Long themeId, String type, int i) {
        return String.format("%s/%s/%s/%s/%s_%s%s", profile, shopId, themeId, type, i, UUID.randomUUID(), EXTENSION);
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new CustomException(INTERNAL_SERVER_ERROR);
        }
    }
}
