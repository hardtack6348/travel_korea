package kr.co.mycom.travel_korea.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWT;
import kr.co.mycom.travel_korea.config.JwtConfig;
import kr.co.mycom.travel_korea.config.SecurityConfig;
import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.repository.UserRepository;
import kr.co.mycom.travel_korea.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final JwtConfig jwt;
    private final SecurityConfig security;
    private final UserRepository repo;

    public UserEntity signup(@RequestBody UserRequest userInput) {
        UserEntity rep = new UserEntity();
        rep.setEmail(userInput.getEmail());
        rep.setPassword(security.passwordEncoder().encode(userInput.getPassword()));
        rep.setNickname(userInput.getNickname());
        return rep;
    }

    public JwtConfig.TokenResponse login(@RequestBody UserRequest request) throws JOSEException {
        UserEntity dbUser = repo.findByEmail(request.getEmail());
        if (dbUser ==null) return null;
        if (security.passwordEncoder().matches(request.getPassword(), dbUser.getPassword())) {
//       로그인 성공
//       완료 페이지가 어떻게 될지 몰라서 이메일만 보냄)
         return jwt.createTokenPair(dbUser.getEmail());
        }
        return null;
    }

    public void logout() throws Exception {
//        엑세스 토큰 삭제
        jwt.DeleteToken();
    }

    public JwtConfig.TokenResponse refresh(UserRequest request) {
//        엑세스 토큰 재발급
        return null;
    }

    public UserEntity emailVerfication(UserRequest request) {
//        이메일 인증번호 발송
        return null;
    }

    public UserEntity emailVerficationConfirm(UserRequest request) {
//        이메일 인증번호 확인
        return null;
    }

    public UserEntity passwordResetRequest(UserRequest request) {
//        비밀번호 재설정요청
        return null;
    }

    public UserEntity changePassword(UserRequest request) {
//        비밀번호 변경
        return null;
    }
}

