package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Comment")
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "COMMENT_ID",nullable = false)
    private Long id;

}
