package kr.co.mycom.travel_korea.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.internal.Abstract;

@Data
@Abstract
@NoArgsConstructor
public class CoursePlaceRequest {
    private Integer CoursePlaceId;
    private String CoursePlaceName;
}
