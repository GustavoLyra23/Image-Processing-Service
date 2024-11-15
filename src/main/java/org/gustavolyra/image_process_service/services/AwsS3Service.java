package org.gustavolyra.image_process_service.services;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.gustavolyra.image_process_service.config.S3Config;
import org.gustavolyra.image_process_service.models.dto.ImageDataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class AwsS3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Config amazonS3;


    public AwsS3Service(S3Config amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String sendFileToS3(ImageDataDto message) throws IOException {
        log.info("Adding file to bucket");
        String fileName = UUID.randomUUID() + "_" + message.getFileName();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(message.getContentType());
        metadata.setContentDisposition("inline");
        metadata.setContentLength(message.getFileData().length);

        InputStream inputStream = new ByteArrayInputStream(message.getFileData());
        amazonS3.amazonS3().putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
        return amazonS3.amazonS3().getUrl(bucketName, fileName).toString();
    }

    public byte[] fetchFileFromS3(String fileName) throws IOException {
        log.info("Fetching file from bucket");
        S3Object s3Object = amazonS3.amazonS3().getObject(new GetObjectRequest(bucketName, fileName));
        try (InputStream inputStream = s3Object.getObjectContent()) {
            return inputStream.readAllBytes();
        }
    }
}
