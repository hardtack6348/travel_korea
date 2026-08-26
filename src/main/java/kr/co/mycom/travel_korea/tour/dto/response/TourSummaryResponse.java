package kr.co.mycom.travel_korea.tour.dto.response;

public record TourSummaryResponse(
        String contentId,
        String contentTypeId,
        String title,
        String address,
        String image,
        String thumbnail,
        String areaCode,
        String sigunguCode,
        Double latitude,
        Double longitude
) {

}
