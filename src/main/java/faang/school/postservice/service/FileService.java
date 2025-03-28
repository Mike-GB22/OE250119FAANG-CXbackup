package faang.school.postservice.service;

import com.fasterxml.jackson.datatype.jdk8.WrappedIOException;
import faang.school.postservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String FILE_KEY_PATTERN= "USER_ID_%s/%s-%s";
    private static final String DEFAULT_USER_ID = "DEFAULT";

    private final S3Client s3Client;
    private final UserContext userContext;


    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;


    public String uploadFile(MultipartFile file)  {
        String fileKey = getFileKey(file);

        try {
            s3Client.putObject(
                    getPutObjectRequest(file, fileKey), getRequestBody(file));
        } catch (IOException e) {
            throw new WrappedIOException(e);
        }

        return fileKey;
    }

    public InputStream downloadFile(String fileKey) {
        return s3Client.getObject(
                getGetObjectResponse(fileKey));
    }

    private PutObjectRequest getPutObjectRequest(MultipartFile file, String fileKey) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();
    }

    private RequestBody getRequestBody(MultipartFile file) throws IOException {
        return RequestBody.fromInputStream(file.getInputStream(), file.getSize());
    }

    private GetObjectRequest getGetObjectResponse(String fileKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
    }

    private String getFileKey(MultipartFile file) {
        String pathName = DEFAULT_USER_ID;
        String timeStamp = String.valueOf(System.nanoTime());

        return String.format(FILE_KEY_PATTERN, pathName, timeStamp, file.getOriginalFilename());
    }
}
