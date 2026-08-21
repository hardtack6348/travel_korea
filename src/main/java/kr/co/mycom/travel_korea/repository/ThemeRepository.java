package kr.co.mycom.travel_korea.repository;

import kr.co.mycom.travel_korea.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<UserEntity,Long> {
}
