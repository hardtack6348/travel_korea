package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookmark")
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkEntity {
    @Id
    @Column(name = "CONTENT_ID",nullable = false)
    private Long content_id;
    @Id
    @Column(name = "USER_ID",nullable = false)
    private Long user_id;
    @Column(name = "CREATED_AT")
    private LocalDateTime created_at;

}
