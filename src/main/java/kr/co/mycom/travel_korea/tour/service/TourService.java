package kr.co.mycom.travel_korea.tour.service;

import kr.co.mycom.travel_korea.tour.dto.response.TourListResponse;
import kr.co.mycom.travel_korea.tour.mapper.TourMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourService {
    private final TourApiClient tourApiClient;
    private final TourMapper tourMapper;

    public TourListResponse getTours(int page, int size, Integer areaCode, Integer sigunguCode, Integer contentTypeId, String arrange) {
        var response = tourApiClient.getAreaBasedList(page, size, areaCode, sigunguCode, contentTypeId, arrange);
        var items = response.items().stream().map(tourMapper::toSummary).toList();

        return new TourListResponse(
                items,
                response.pageNo(),
                response.numOfRows(),
                response.totalCount()
        );
    }

}
