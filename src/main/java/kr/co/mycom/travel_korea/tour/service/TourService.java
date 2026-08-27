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
                            /*
                             * taketime이 없을 경우 다른 코스 정보를 이용해
                             * 대체 일정을 생성합니다.
                             */
                            makeCourseDuration(intro, stops),
                            makeCourseDescription(intro),
                            stops
                    );
                })
                .toList();
    }

    /**
     * 코스 소요시간 정보를 생성합니다.
     *
     * taketime이 있으면 해당 값을 사용하고,
     * 없으면 distance 또는 경유지 개수를 이용한
     * 대체 문구를 반환합니다.
     */
    private String makeCourseDuration(TourApiCourseIntroItem intro, List<String> stops) {
        /*
         * detailIntro2 결과가 존재하면
         * 실제 소요시간을 가장 먼저 확인합니다.
         */
        if (intro != null) {
            String takeTime = emptyToNull(intro.taketime());

            if (takeTime != null) {
                return takeTime;
            }

            /*
             * 소요시간은 없지만 코스 거리가 있다면
             * 거리 정보를 대신 표시합니다.
             */

            String distance = emptyToNull(intro.distance());

            if (distance != null) {
                return "코스 " + distance;
            }
        }

        /*
         * 소요시간과 거리 정보가 모두 없지만
         * 경유지가 있다면 코스 장소 개수를 표시합니다.
         */
        if (stops != null && !stops.isEmpty()) {
            return "주요 장소" + stops.size() + "곳";
        }

        /*
         * 사용할 수 있는 정보가 전혀 없는 경우입니다.
         */
        return "상세 일정 확인";
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
     * TourAPI 코스 소개정보를 이용해
     * 메인 카드에 표시할 설명을 생성합니다.
     *
     * theme을 우선 사용하지만,
     * '지자체'와 같은 관리용 문구는 제외합니다.
     *
     * theme을 사용할 수 없으면 schedule을 사용하고,
     * 둘 다 없으면 null을 반환합니다.
     */
    private String makeCourseDescription(
            TourApiCourseIntroItem intro
    ) {
        if (intro == null) {
            return null;
        }

        /*
         * theme 값에서 앞뒤 공백을 제거하고
         * 화면에 표시해도 되는 내용인지 검사합니다.
         */

        String theme = emptyToNull(intro.theme());

        if (isUsableCourseDescription(theme)) {
            return theme.trim();
        }

        /*
         * theme을 사용할 수 없다면
         * 코스 일정 설명인 schedule을 대신 사용합니다.
         */

        String schedule = emptyToNull(intro.schedule());

        if (isUsableCourseDescription(schedule)) {
            return schedule.trim();
        }

        /*
         * theme과 schedule이 모두 없거나
         * 관리용 문구라면 null을 반환합니다.
         *
         * 프론트엔드에서는 null일 때
         * 기본 안내 문구를 표시합니다.
         */
        return null;
    }

    /**
     * 코스 설명을 사용자 화면에 표시할 수 있는지 검사합니다.
     *
     * TourAPI에서 내려오는 구분선과 관리용 표현을 제거한 뒤
     * 실제 설명이 남아 있는지 확인합니다.
     */
    private boolean isUsableCourseDescription(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        /*
         * 하이픈, 밑줄, 공백을 제거해
         * 실제 텍스트 내용만 비교합니다.
         *
         * 예:
         * "----지자체----" -> "지자체"
         */

        String normalized = value
                .replace("-", "")
                .replace("_", "")
                .replaceAll("\\s+", "")
                .trim();

        if (normalized.isBlank()) {
            return false;
        }

        /*
         * 사용자에게 의미가 없는 관리용 값을 제외합니다.
         */
        return !normalized.equals("지자체")
                && !normalized.equals("기타")
                && !normalized.equals("없음");
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
