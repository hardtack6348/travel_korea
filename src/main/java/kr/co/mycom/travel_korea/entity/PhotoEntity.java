package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PHOTO_ID",nullable = false)
    private Long id;
    private Long contentId;
    private String originalFileName;
    private String storagePath;
    private String mimeType;
    private String sortOrder;

}
