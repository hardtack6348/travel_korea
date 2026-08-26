package kr.co.mycom.travel_korea.tour.client;

import kr.co.mycom.travel_korea.tour.dto.external.TourApiItem;
import kr.co.mycom.travel_korea.tour.dto.external.TourApiResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 실제 TourAPI가 완성되기 전 사용하는 임시 데이터 제공자입니다.
 *
 * local-mock 프로필에서만 사용됩니다.
 *
 * TODO(팀원 A):
 * 실제 TourApiClientImpl이 완성되면 기본 실행에서는
 * 실제 구현체를 사용하도록 변경해 주세요.
 */
@Component
@Profile("local-mock")
public class MockTourApiClient implements TourApiClient {

    @Override
    public TourApiResponse getAreaBasedList(
            int page,
            int size,
            Integer lDongRegnCd,
            Integer lDongSignguCd,
            Integer contentTypeId,
            String arrange
    ) {
        /*
         * 지금은 요청 조건을 실제로 처리하지 않고
         * 추천 여행지 3개를 고정으로 반환합니다.
         *
         * TODO(팀원 A):
         * 실제 구현체에서 page, size, lDongRegnCd,
         * lDongSignguCd, contentTypeId, arrange를
         * TourAPI Query Parameter로 전달해 주세요.
         */

        List<TourApiItem> items = List.of(
                new TourApiItem(
                        "126508",
                        "12",
                        "경복궁",
                        "서울특별시 종로구",
                        "사직로 161",
                        null,
                        null,
                        "11",
                        "110",
                        "126.9770170625",
                        "37.5788222356"
                ),
                new TourApiItem(
                        "126485",
                        "12",
                        "비자림",
                        "제주특별자치도 제주시 구좌읍",
                        "비자숲길 55",
                        null,
                        null,
                        "50",
                        "110",
                        "126.8114078",
                        "33.4913452"
                ),
                new TourApiItem(
                        "125476",
                        "12",
                        "경포해변",
                        "강원특별자치도 강릉시",
                        "창해로",
                        null,
                        null,
                        "51",
                        "150",
                        "128.9071180",
                        "37.8056495"
                )
        );

        return new TourApiResponse(
                items,
                page,
                size,
                items.size()
        );
    }
}