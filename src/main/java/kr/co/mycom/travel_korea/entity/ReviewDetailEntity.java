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
@Table(name = "review_detail")
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailEntity {
    @Id
    @Column(name = "CONTENT_ID",nullable = false)
    private Long content_id;
    @Column(name = "DATE_START",nullable = false)
    private String date_start;
    @Column(name = "DATE_END",nullable = false)
    private String date_end;
    @Column(name = "TOUR_LOC",nullable = false)
    private String tour_loc;
    @Column(name="TOUR_LOC_MAIN",length=50)
    private String tour_loc_main;
}
