package kr.co.mycom.travel_korea.tour.dto.response;

/** 특정 광역지역에 속한 시군구 선택 항목입니다. */
public record TourDistrictResponse(
        String lDongRegnCd,
        String lDongSignguCd,
        String name,
        String regionName
) {
}
