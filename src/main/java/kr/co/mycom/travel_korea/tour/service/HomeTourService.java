package kr.co.mycom.travel_korea.tour.service;

import kr.co.mycom.travel_korea.tour.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 메인 페이지에 필요한 관광 데이터를 조합합니다.
 *
 * 첫 번째 목표 : 추천 여행지 3개만 반환
 */
@Service
@RequiredArgsConstructor
public class HomeTourService {
    private final TourService tourService;

    public HomeTourResponse getHomeTours() {
        /*
         * 추천 여행지는 관광지 contentTypeId=12 중 3개를 조회합니다.
         *
         * TODO(팀원 A):
         * 실제 TourAPI 정렬 기준이 적용되는지 확인해 주세요.
         * 현재 Q는 수정일순 정렬로 사용할 예정입니다.
         */

        List<TourSummaryResponse> destinations = tourService.getTours(1,3,null,null,12,"Q").items();

        /*
         * 1 : 첫 번째 페이지
         * 3 : 데이터 3개
         * null : 전체 지역
         * null : 전체 시군구
         * 12 : 관광지
         * "Q" : 수정일순 정렬
         */

        /*
         * 추천 코스, 즐길거리, 축제는 아직 구현하지 않습니다.
         * 추천 여행지 연결이 성공한 뒤 하나씩 추가합니다.
         */

        return new HomeTourResponse(destinations, List.of(), List.of(), List.of());
    }

//    private List<TourEnjoyResponse> getEnjoyItems() {
//        List<TourEnjoyResponse> result = new ArrayList<>();
//
//        addEnjoyItem(result, 15, "축제·행사");
//        addEnjoyItem(result, 28, "레포츠");
//        addEnjoyItem(result, 39, "음식점");
//        addEnjoyItem(result, 38, "쇼핑");
//        addEnjoyItem(result, 32, "숙박");
//
//        return result;
//    }
//
//    private void addEnjoyItem(List<TourEnjoyResponse> result, int contentTypeId, String category) {
//        var response = tourService.getTours(1,1,null,null, contentTypeId, "Q");
//
//        if (response.items().isEmpty()) {
//            return;
//        }
//
//        var item = response.items().get(0);
//
//        result.add(new TourEnjoyResponse(
//                item.contentId(),
//                item.contentTypeId(),
//                category,
//                item.image(),
//                item.title(),
//                item.address()
//        ));
//    }
}
