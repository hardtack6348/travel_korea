package kr.co.mycom.travel_korea.tour.dto.request;

public record TourSearchRequest (
  Integer page,
  Integer size,
  Integer lDongRegnCd,
  Integer lDongSignguCd,
  Integer contentTypeId,
  String arrange
) {
    public TourSearchRequest {
        page = page == null ? 1 : page;
        size = size == null ? 9 : size;
        arrange = arrange == null || arrange.isBlank() ? "Q" : arrange;
    }
}


// TODO(본인):
// page, size, contentTypeId, arrange에 대한 검증을 추가합니다.