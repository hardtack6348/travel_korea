package kr.co.mycom.travel_korea.service;

import jakarta.transaction.Transactional;
import kr.co.mycom.travel_korea.entity.CoursePlaceEntity;
import kr.co.mycom.travel_korea.repository.CoursePlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CoursePlaceService {

    //public static List<CoursePlaceEntity> get;
    //public static CoursePlaceEntity add;
    private final CoursePlaceRepository coursePlaceRepository;

    public CoursePlaceEntity add(CoursePlaceEntity coursePlaceEntity) {
        return coursePlaceRepository.save(coursePlaceEntity);
    }

    public List<CoursePlaceEntity> get() {
        return coursePlaceRepository.findAll();
    }

    public void drop(Integer courseId) {
        coursePlaceRepository.deleteById(courseId);

    }

    public CoursePlaceEntity modify(Integer courseId, Integer placeId, CoursePlaceEntity coursePlaceEntity) {
        coursePlaceEntity.setCourseId(courseId);
        coursePlaceEntity.setCoursePlaceId(placeId);
        return coursePlaceRepository.save(coursePlaceEntity);
    }
}
