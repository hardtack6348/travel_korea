package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comment_like")
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeEntity {
    @Id
    private Long user_id;

    @Column(nullable = false)
    private Long comment_id;

    @Column(nullable = false)
    private LocalDateTime created_at;
}
