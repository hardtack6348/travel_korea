package kr.co.mycom.travel_korea.controller;

import kr.co.mycom.travel_korea.entity.CoursePlaceEntity;
import kr.co.mycom.travel_korea.service.CoursePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CoursePlaceController {
    private final CoursePlaceService coursePlaceService;


    @GetMapping("/api/v1/courses")
    public List<CoursePlaceEntity> findAll() {
        return coursePlaceService.get();
    }

    @PostMapping("/api/v1/admin/courses")
    public CoursePlaceEntity GetCoursePlaceEntityById( @RequestBody  CoursePlaceEntity coursePlaceEntity) {
        return coursePlaceService.add(coursePlaceEntity);

    }
    @DeleteMapping("/api/v1/admin/courses/{courseId}")
    public void GetCoursePlaceEntityByCourseId(@PathVariable Integer courseId) {
         coursePlaceService.drop( courseId);
    }
    @PatchMapping("/api/v1/admin/courses/{courseId}/places/{placeId}")
    public CoursePlaceEntity GetCoursePlaceEntityById(@PathVariable  Integer courseId, @PathVariable Integer placeId,
                                                      @RequestBody  CoursePlaceEntity coursePlaceEntity) {
        return coursePlaceService.modify(courseId, placeId, coursePlaceEntity);
    }

}

