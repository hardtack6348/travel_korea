package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CommentClass.class)
public class CommentLikeEntity {
    @Id
    private Long id;
    @Id
    private Long commentId;

    private Date date;



}
