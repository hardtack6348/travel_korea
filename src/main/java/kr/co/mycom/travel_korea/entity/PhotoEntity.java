package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "photos")
@NoArgsConstructor
@AllArgsConstructor
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photo_id;

    @Column(nullable = false)
    private Long content_id;

    @Column(length = 255)
    private String original_file_name;

    @Column(nullable = false,length = 255)
    private String storage_path;

    @Column(length = 30)
    private String mime_type;

    @Column(nullable = false)
    private Integer sort_order;

    private String search_keyword;
}
