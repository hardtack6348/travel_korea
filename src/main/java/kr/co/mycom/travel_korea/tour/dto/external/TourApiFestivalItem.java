package kr.co.mycom.travel_korea.tour.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** searchFestival2 결과 중 목록 화면에서 사용하는 필드입니다. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiFestivalItem(
        String contentid,
        String contenttypeid,
        String title,
        String addr1,
        String addr2,
        String firstimage,
        String firstimage2,
        String lDongRegnCd,
        String lDongSignguCd,
        String mapx,
        String mapy,
        String eventstartdate,
        String eventenddate
) {
}
