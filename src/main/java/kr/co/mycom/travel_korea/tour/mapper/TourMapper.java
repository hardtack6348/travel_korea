package kr.co.mycom.travel_korea.tour.mapper;

import kr.co.mycom.travel_korea.tour.dto.response.TourSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TourMapper {
    public TourSummaryResponse toSummary (TourApiItem item) {
        return new TourSummaryResponse(
                item.contentid(),
                item.contenttypeid(),
                item.title(),
                combineAddress(item.addr1(), item.addr2()),
                emptyToNull(item.firstimage()),
                emptyToNull(item.firstimage()),
                item.areacode(),
                item.sigungucode(),
                parseDouble(item.mapy()),
                parseDouble(item.mapx())
        );
    }


    private String combineAddress(String addr1, String addr2) {
        return Stream.of(addr1, addr2).filter(value -> value != null && !value.isBlank()).collect(Collectors.joining(" "));
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
