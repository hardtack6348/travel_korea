package kr.co.mycom.travel_korea.repository;

import kr.co.mycom.travel_korea.entity.CoursePlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursePlaceRepository extends JpaRepository<CoursePlaceEntity, Integer> {
    // 테이블 <===> Entity
    // Entity에 CRUD   ==> Repository 인터페이스로 선언 ==> 구현 자동


}
