package kr.co.mycom.travel_korea.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="post_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    private String objectKey;
    private String originalFileName;
    private String contentType;
    private Long size;
    public PostImage(String s,String s1,String s2, long size) {
        this.objectKey = s;
        this.originalFileName = s1;
        this.contentType = s2;
        this.size = size;
    }

    public void add(PostImage images) {
    images.setPost(post);
    images.setOriginalFileName(originalFileName);
    images.setContentType(contentType);
    images.setSize(size);
    }
}