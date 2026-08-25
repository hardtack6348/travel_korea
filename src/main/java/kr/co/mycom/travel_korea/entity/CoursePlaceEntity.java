package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="coursePlace")
@AllArgsConstructor
@NoArgsConstructor
public class CoursePlaceEntity {
    @Id
    @Column(name = "COURSE_PLACE_ID")
    private Integer coursePlaceId;
    private Integer sequenceNo;
    private Integer courseId;
    private String placeName;
    private Integer latitude;
    private Integer longitude;
    private Integer visited;
}
