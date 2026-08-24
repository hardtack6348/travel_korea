package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "content")
@NoArgsConstructor
@AllArgsConstructor
public class ContentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long content_id;

    @Column(nullable = false)
    private Long user_id;

    @Column(nullable = false ,length = 20)
    private String content_type;

    @Column(length = 2000)
    private String content;

    @Column(length = 60)
    private String title;

    @Column(nullable = false ,length = 20)
    private String visibility;

    @Column(nullable = false)
    private Long view_count;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;
}
