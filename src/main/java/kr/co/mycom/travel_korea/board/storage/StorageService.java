package kr.co.mycom.travel_korea.board.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    StoredObject upload(MultipartFile file);
    void delete(String objectKey);
    String createReadUrl(String objectKey);
}
