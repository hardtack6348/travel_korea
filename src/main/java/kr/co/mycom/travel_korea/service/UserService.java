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
    private UserRepository repo;

    public UserEntity findUserInfo(Long id) {
        UserEntity user = new UserEntity();
        return user;
    }
}
