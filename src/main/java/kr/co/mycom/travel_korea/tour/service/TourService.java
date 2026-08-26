package kr.co.mycom.travel_korea.tour.service;

import kr.co.mycom.travel_korea.tour.client.TourApiClient;
import kr.co.mycom.travel_korea.tour.dto.external.TourApiCourseIntroItem;
import kr.co.mycom.travel_korea.tour.dto.response.TourCourseResponse;
import kr.co.mycom.travel_korea.tour.dto.response.TourListResponse;
import kr.co.mycom.travel_korea.tour.mapper.TourMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 메인 화면에서 사용할 추천 여행코스를 조회합니다.
     *
     * 먼저 areaBasedList2에서 여행코스 기본정보를 조회한 뒤,
     * 각 코스의 detailIntro2와 detailInfo2를 추가로 조회합니다.
     */
    public List<TourCourseResponse> getRecommendedCourses(
            int size
    ) {
        /*
         * contentTypeId=25:
         * TourAPI의 여행코스 유형입니다.
         *
         * arrange=Q:
         * 대표 이미지가 있는 최근 수정 코스를 조회합니다.
         */
        var courseList = tourApiClient.getAreaBasedList(
                1,
                size,
                null,
                null,
                25,
                "Q"
        );

        return courseList.items()
                .stream()
                .map(item -> {
                    var intro = tourApiClient.getCourseIntro(
                            item.contentid()
                    );

                    var details = tourApiClient.getCourseDetails(
                            item.contentid()
                    );

                    /*
                     * detailInfo2의 subname을 화면의 경유지 목록으로 변환합니다.
                     *
                     * 현재 카드 디자인은 최대 4개의 경유지를 표시하므로
                     * 앞에서부터 4개만 사용합니다.
                     */
                    List<String> stops = details.stream()
                            .map(detail -> detail.subname())
                            .filter(name ->
                                    name != null &&
                                            !name.isBlank()
                            )
                            .limit(4)
                            .toList();

                    return new TourCourseResponse(
                            item.contentid(),
                            item.contenttypeid(),
                            firstNonBlank(
                                    item.firstimage(),
                                    item.firstimage2()
                            ),
                            item.title(),
                            extractRegion(item.addr1()),
                            intro == null
                                    ? null
                                    : emptyToNull(
                                    intro.taketime()
                            ),
                            makeCourseDescription(intro),
                            stops
                    );
                })
                .toList();
    }

    /**
     * 여러 이미지 중 처음으로 값이 있는 이미지를 선택합니다.
     */
    private String firstNonBlank(
            String first,
            String second
    ) {
        if (first != null && !first.isBlank()) {
            return first;
        }

        if (second != null && !second.isBlank()) {
            return second;
        }

        return null;
    }

    /**
     * 전체 주소에서 화면에 사용할 지역명을 추출합니다.
     *
     * 예:
     * "부산광역시 해운대구" → "부산광역시"
     */
    private String extractRegion(String address) {
        if (address == null || address.isBlank()) {
            return "전국";
        }

        return address.split("\\s+")[0];
    }

    /**
     * 코스 소개정보에서 카드 설명을 생성합니다.
     *
     * theme이 있으면 우선 사용하고,
     * 없으면 schedule을 사용합니다.
     */
    private String makeCourseDescription(
            TourApiCourseIntroItem intro
    ) {
        if (intro == null) {
            return null;
        }

        if (intro.theme() != null &&
                !intro.theme().isBlank()) {
            return intro.theme();
        }

        return emptyToNull(intro.schedule());
    }

    /**
     * 빈 문자열을 null로 변환합니다.
     */
    private String emptyToNull(String value) {
        return value == null || value.isBlank()
                ? null
                : value;
    }
    // TODO(본인):
    // 검색 기능을 추가할 때 searchTours() 메서드를 구현합니다.

    // TODO(본인):
    // 상세 기능을 추가할 때 getTourDetail() 메서드를 구현합니다.

    // TODO(팀원 A):
    // TourAPI 오류는 TourApiException으로 변환해 Client에서 전달해 주세요.
}
