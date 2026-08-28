package kr.co.mycom.travel_korea.tour.data;

import kr.co.mycom.travel_korea.tour.dto.response.TourClassificationResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class LocalClassificationCatalog {
    private final List<TourClassificationResponse>
            classifications;

    public LocalClassificationCatalog(
            ObjectMapper objectMapper
    ) {
        ClassPathResource resource =
                new ClassPathResource(
                        "tour/classification-codes.json"
                );

        try (InputStream inputStream =
                     resource.getInputStream()) {

            this.classifications =
                    List.copyOf(
                            objectMapper.readValue(
                                    inputStream,
                                    new TypeReference<
                                            List<TourClassificationResponse>
                                            >() {
                                    }
                            )
                    );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "분류체계 로컬 데이터를 읽지 못했습니다.",
                    exception
            );
        }
    }

    public List<TourClassificationResponse> getAll() {
        return classifications;
    }

}
