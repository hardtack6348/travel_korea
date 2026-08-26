package kr.co.mycom.travel_korea.tour.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * TourAPI 목록 조회 결과 중 WayLog에서 우선 사용하는 필드입니다.
 *
 * TourAPI는 이 필드들 외에도 전화번호, 우편번호, 생성일,
 * 수정일 등 여러 데이터를 반환한다.
 *
 * 현재 메인 화면의 추천 여행지 카드에 필요한 필드만 정의
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiItem(
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
        String mapy
) {
}

/**
 * mapx = 경도 = longitude
 * mapy = 위도 = latitude
 */