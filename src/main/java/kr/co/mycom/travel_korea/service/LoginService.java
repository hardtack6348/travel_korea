package kr.co.mycom.travel_korea.service;

import kr.co.mycom.travel_korea.config.SecurityConfig;
import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final SecurityConfig security;

    public UserEntity signup(@RequestBody UserRequest userInput) {
        UserEntity rep = new UserEntity();
        rep.setEmail(userInput.getEmail());
        rep.setPassword(security.passwordEncoder().encode(userInput.getPassword()));
        rep.setNickname(userInput.getNickname());
        return rep;
    }

    public UserEntity login(@RequestBody UserRequest userInput) {
        UserEntity rep = new UserEntity();
        return rep;
    }

    public UserEntity logout() {
        return null;
    }

    public UserEntity refresh(UserRequest request) {
        return null;
    }

    public UserEntity emailVerfication(UserRequest request) {
        return null;
    }

    public UserEntity emailVerficationConfirm(UserRequest request) {
        return null;
    }

    public UserEntity passwordResetRequest(UserRequest request) {
        return null;
    }

    public UserEntity changePassword(UserRequest request) {
        return null;
    }
}

