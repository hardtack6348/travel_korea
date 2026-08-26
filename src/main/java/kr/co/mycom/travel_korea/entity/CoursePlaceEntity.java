package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "course_place")
@NoArgsConstructor
@AllArgsConstructor
public class CoursePlaceEntity {
    @Id
    private int course_place_id;
    @Id
    @Column(nullable = false)
    private Long course_id;

    @Column(nullable = false)
    private Integer sequence_no;
    @Column(nullable = false ,length = 100)
    private String place_name;
    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    private java.sql.Timestamp visited_at;
}
