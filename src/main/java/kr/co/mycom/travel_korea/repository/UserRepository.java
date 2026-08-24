package kr.co.mycom.travel_korea.repository;

import kr.co.mycom.travel_korea.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByEmail(String email);
    boolean existsByNickname(String NickName);
    boolean existsByEmail(String email);
}
