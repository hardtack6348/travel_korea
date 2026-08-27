package kr.co.mycom.travel_korea.tour.service;

import kr.co.mycom.travel_korea.tour.client.TourApiClient;
import kr.co.mycom.travel_korea.tour.dto.response.TourDistrictResponse;
import kr.co.mycom.travel_korea.tour.dto.response.TourRegionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TourRegionService {
    private static final Map<String, List<Integer>> REGION_GROUPS = Map.of(
            "gyeonggi-incheon", List.of(41, 28),
            "chungcheong", List.of(43, 44, 30, 36110),
            "jeolla", List.of(12, 52),
            "gyeongsang", List.of(47, 48, 26, 27, 31)
    );

    private final TourApiClient tourApiClient;

    @Cacheable(cacheNames = "tourLists", key = "'regions'", sync = true)
    public List<TourRegionResponse> getRegions() {
        return tourApiClient.getRegionCodes(null).stream()
                .map(item -> new TourRegionResponse(item.lDongRegnCd(), item.lDongRegnNm()))
                .toList();
    }

    @Cacheable(
            cacheNames = "tourLists",
            key = "'districts:' + #lDongRegnCd + ':' + #regionGroup",
            sync = true
    )
    public List<TourDistrictResponse> getDistricts(Integer lDongRegnCd, String regionGroup) {
        if (lDongRegnCd != null && regionGroup != null && !regionGroup.isBlank()) {
            throw new IllegalArgumentException("지역 코드와 권역은 동시에 사용할 수 없습니다.");
        }
        List<Integer> codes;
        if (lDongRegnCd != null) {
            codes = List.of(lDongRegnCd);
        } else {
            codes = REGION_GROUPS.get(regionGroup);
            if (codes == null) throw new IllegalArgumentException("지원하지 않는 regionGroup입니다.");
        }

        return codes.stream()
                .flatMap(code -> tourApiClient.getRegionCodes(code).stream())
                .filter(item -> item.lDongSignguCd() != null && !item.lDongSignguCd().isBlank())
                .map(item -> new TourDistrictResponse(
                        item.lDongRegnCd(), item.lDongSignguCd(),
                        item.lDongSignguNm(), item.lDongRegnNm()
                ))
                .toList();
    }
}
