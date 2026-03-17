package org.example.tallemalle_backend.upload;

import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CloudUploadService implements UploadService {
    @Value("${spring.cloud.aws.s3.bucket}")
    private String s3BucketName;
    @Value("${spring.cloud.aws.region.static}")
    private String region;
    private final S3Operations s3Operations;
    private final S3Presigner s3Presigner;

    public String saveFile(MultipartFile file) throws SQLException, IOException {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String filePath = date + "/" + UUID.randomUUID() + "_" +file.getOriginalFilename();
        S3Resource s3Resource = s3Operations.upload(s3BucketName, filePath, file.getInputStream());
        return s3Resource.getURL().toString();
    }

    @Override
    public PresignedUploadDto.PresignRes presign(PresignedUploadDto.PresignReq req) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String filePath = date + "/" + UUID.randomUUID() + "_" + req.getFileName();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3BucketName)
                .key(filePath)
                .contentType(req.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
        String publicUrl = "https://" + s3BucketName + ".s3." + region + ".amazonaws.com/" + filePath;

        return PresignedUploadDto.PresignRes.builder()
                .uploadUrl(uploadUrl)
                .publicUrl(publicUrl)
                .filePath(filePath)
                .build();
    }
}
