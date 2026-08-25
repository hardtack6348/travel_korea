package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courseDetail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailEntity {
    @Id
    @Column(name = "CONTENT_ID")
    private Long contentId;
    private Integer enteredAt;
}
