package kr.co.mycom.travel_korea.service;

import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.repository.UserRepository;
import kr.co.mycom.travel_korea.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;

    public UserEntity findUserInfo(String email) {
        UserEntity user = new UserEntity();
        if(existEmail(email)){
            UserEntity user1 = repo.findByEmail(email);
            user.setEmail(user1.getEmail());
        }
        return user;
    }
    public boolean existNickname(String nickname) {
        if (repo.existsByNickname(nickname)){
            return true;
        }
        return false;
    }
    public boolean existEmail(String email) {
        if (repo.existsByEmail(email)){
            return true;
        }
        return false;
    }
}
