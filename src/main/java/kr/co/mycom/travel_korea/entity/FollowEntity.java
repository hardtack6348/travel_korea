package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "follow")
@NoArgsConstructor
@AllArgsConstructor
public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long follower_id;

    @Column(nullable = false)
    private Long following_id;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime created_at;
}
