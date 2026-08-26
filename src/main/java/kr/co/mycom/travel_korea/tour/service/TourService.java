package kr.co.mycom.travel_korea.tour.service;

import kr.co.mycom.travel_korea.tour.client.TourApiClient;
import kr.co.mycom.travel_korea.tour.dto.response.TourListResponse;
import kr.co.mycom.travel_korea.tour.mapper.TourMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * WayLog 관광정보 조회 기능의 비즈니스 로직을 담당합니다.
 *
 * 외부 TourAPI를 직접 호출하지 않고 TourApiClient 인터페이스를
 * 통해 데이터를 조회합니다.
 */

@Service
@RequiredArgsConstructor
public class TourService {
    private final TourApiClient tourApiClient;
    private final TourMapper tourMapper;


    // 관광정보 목록을 조회하고 WayLog 응답 형식으로 변환
    public TourListResponse getTours(int page, int size, Integer lDongRegnCd, Integer lDongSignguCd, Integer contentTypeId, String arrange) {

        validateRequest(page, size, lDongRegnCd, lDongSignguCd);
        var response = tourApiClient.getAreaBasedList(page, size, lDongRegnCd, lDongSignguCd, contentTypeId, arrange);
        var items = response.items().stream().map(tourMapper::toSummary).toList();

        return new TourListResponse(
                items,
                response.pageNo(),
                response.numOfRows(),
                response.totalCount()
        );
    }

    // 기본적인 목록 요청값을 검증
    private void validateRequest(int page, int size, Integer lDongRegnCd, Integer lDongSignguCd) {
        if (page < 1) {
            throw new IllegalArgumentException("page는 1 이상이어야 합니다.");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size는 1 이상 100 이하여야 합니다.");
        }

        if (lDongRegnCd == null && lDongSignguCd != null) {
            throw new IllegalArgumentException("lDongSignguCd 사용하려면 lDongRegnCd가 필요합니다.");
        }
    }

    // TODO(본인):
    // 검색 기능을 추가할 때 searchTours() 메서드를 구현합니다.

    // TODO(본인):
    // 상세 기능을 추가할 때 getTourDetail() 메서드를 구현합니다.

    // TODO(팀원 A):
    // TourAPI 오류는 TourApiException으로 변환해 Client에서 전달해 주세요.
}
