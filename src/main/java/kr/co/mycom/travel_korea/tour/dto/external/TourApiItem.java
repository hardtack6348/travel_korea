package kr.co.mycom.travel_korea.tour.dto.external;

/**
 * TourAPI 목록 조회 결과 중 WayLog에서 우선 사용하는 필드입니다.
 *
 * 필드명은 TourAPI 원본 응답의 이름을 유지합니다.
 * WayLog 응답 필드로 바꾸는 작업은 TourMapper에서 처리합니다.
 *
 * TODO(팀원 A):
 * 실제 TourAPI 응답 구조를 확인한 후 필드 타입과 누락 필드를 검토해 주세요.
 */


public record TourApiItem(String contentid, String contenttypeid, String title, String addr1, String addr2, String firstimage, String firstimage2, String areacode, String sigungucode, String mayx, String mapy) {
}

/**
 * mapx = 경도 = longitude
 * mapy = 위도 = latitude
 */