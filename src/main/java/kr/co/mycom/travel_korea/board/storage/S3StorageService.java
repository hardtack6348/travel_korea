package kr.co.mycom.travel_korea.board.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@ConditionalOnProperty(
        name="app.storage.type", havingValue="s3",matchIfMissing=true
)
public class S3StorageService implements StorageService{
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jepg","image/png","image/gif","image/webp");
    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucket;
    private final Long maxBytes;
    private final Duration readUrlDuration;


    public S3StorageService(S3Client s3Client, S3Presigner presigner,
                            @Value("${app.s3.bucket}")String bucket,
    @Value("${app.max-file-size-byte}")Long maxBytes,
    @Value("${app.s3.presigned-url-minates}") Long readUrlMinutes) {
        this.s3Client = s3Client;
        this.presigner =presigner;
        this.bucket = bucket;
        this.maxBytes =  maxBytes;
        this.readUrlDuration = Duration.ofMinutes(readUrlMinutes);
    }

    @Override
    public StoredObject upload(MultipartFile file)  {
        validate(file);
        String original = sanitizeFilename(file.getOriginalFilename());
        String contentType = file.getContentType();
        String key = "myboard/"+UUID.randomUUID()+extensionFor(contentType);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();
        try {
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("이미지파일 읽기 실패",e);
        }
        return new StoredObject(key,original,contentType,file.getSize());
    }

    private String extensionFor(String contentType) {
        return switch (contentType){
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> "";
        };
    }

    private void validate(MultipartFile file){
        if(file== null || file.isEmpty()){
            System.out.println("파일이 비어있습니다.");
        }
        if(file.getSize() > maxBytes) {
            System.out.println("파일 용량이 너무 큽니다.");
        }
        if(file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())){
            System.out.println("jpg,png,gif,webp 이미지만 업로드 할 수 있습니다.");
        }
    }
    // 순수한 파일명만 반환시키기
    // ex) c://test/veryLongLocaltion.../photo1.png => photo1.png
    private  String sanitizeFilename(String filename){
        if (filename == null || filename.isBlank()) return "image";
        String nomalized = filename.replace('\\','/');
        int index = nomalized.lastIndexOf('/');
        return index >= 0 ? nomalized.substring(index + 1) : nomalized;

    }
    @Override
    public void delete(String objectKey) {
        if(objectKey == null || objectKey.isBlank()) return ;
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build());

    }

    @Override
    public String createReadUrl(String objectKey) {
       GetObjectRequest getObjectRequest = GetObjectRequest.builder()
               .bucket(bucket)
               .key(objectKey)
               .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
               .signatureDuration(readUrlDuration)
                .getObjectRequest(getObjectRequest)
                .build();
        return presigner.presignGetObject(presignRequest).url().toString();
    }
}
