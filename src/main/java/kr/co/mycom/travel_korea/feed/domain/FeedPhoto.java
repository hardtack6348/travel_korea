package kr.co.mycom.travel_korea.feed.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feed_photo")
public class FeedPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_photo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feed_post_id", nullable = false)
    private FeedPost feedPost;

    /*
     * 실제 이미지 파일을 DB에 넣지 않고
     * S3, 로컬 스토리지 등에 저장된 이미지 주소만 저장합니다.
     */
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public FeedPhoto(FeedPost feedPost, String imageUrl, int sortOrder) {
        this.feedPost = feedPost;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}
