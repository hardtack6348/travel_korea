package kr.co.mycom.travel_korea.tour.dto.response;

/**
 * 메인 페이지 '이번 주 여행 소식'에서 사용하는 축제 응답입니다.
 */

public record TourFestivalResponse(
        String contentId,
        String contentTypeId,
        String image,
        String title,
        String startDate,
        String endDate,
        String location
) {
    // TODO(팀원 A):
    // 축제 검색 API의 eventstartdate, eventenddate를 연결해 주세요.

}
