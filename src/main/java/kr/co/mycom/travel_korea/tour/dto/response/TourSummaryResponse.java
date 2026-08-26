package kr.co.mycom.travel_korea.tour.dto.response;

public record TourSummaryResponse(
        String contentId,
        String contentTypeId,
        String title,
        String address,
        String image,
        String thumbnail,
        String lDongRegnCd,
        String lDongSignguCd,
        Double latitude,
        Double longitude
) {

}
