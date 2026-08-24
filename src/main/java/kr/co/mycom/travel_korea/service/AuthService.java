package kr.co.mycom.travel_korea.service;

import com.nimbusds.jose.JOSEException;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import kr.co.mycom.travel_korea.config.JwtConfig;
import kr.co.mycom.travel_korea.config.SecurityConfig;
import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.repository.UserRepository;
import kr.co.mycom.travel_korea.request.UserRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtConfig jwt;
    private final SecurityConfig security;
    private final UserRepository repo;
    private final JavaMailSender emailSender;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    @Value("${spring.mail.auth-code-expiration-millis}")
    private Long authCodeExpiration;
    @Value("${MAIL_USERNAME}")
    private String mailid ;
    @Value("${MAIL_PW}")
    private String mailpw;
    public UserEntity signup(UserRequest userInput) {
        UserEntity rep = new UserEntity();
        rep.setCreated_at(LocalDateTime.now());
        rep.setEmail(userInput.getEmail());
        rep.setPassword(security.passwordEncoder().encode(userInput.getPassword()));
        rep.setNickname(userInput.getNickname());
        rep.setGrade("user");
        return repo.save(rep);


    }

    public JwtConfig.TokenResponse login(@RequestBody UserRequest request) throws JOSEException {
        UserEntity dbUser = repo.findByEmail(request.getEmail());
        if (dbUser ==null) {
            System.out.println("db에 user를 찾을 수 없습니다.");
            return null;
        }
        if (security.passwordEncoder().matches(request.getPassword(), dbUser.getPassword())) {
//       로그인 성공
//       완료 페이지가 어떻게 될지 몰라서 이메일만 보냄
            System.out.println("로그인 성공");
         return jwt.createTokenPair(dbUser.getEmail());
        }
        System.out.println("알 수 없는 오류");
        return null;
    }

    public ResponseCookie logout() {
//        엑세스 토큰 삭제
        return jwt.deleteToken();
    }

    public ResponseEntity<?> refreshToken(String refreshTokenCookie) {
    // 엑세스 토큰 리프레시
        try {
            if (refreshTokenCookie == null || refreshTokenCookie.isBlank()) {
                throw new IllegalArgumentException("Refresh Token이 존재하지 않습니다.");
            }

            // 서명 검증 및 만료 여부 확인
            JwtConfig.RefreshTokenValidationResult result = jwt.validateRefreshTokenWithExpirationCheck(refreshTokenCookie);
            String email = result.email();

            // 1) Refresh Token이 만료된 경우 -> 두 토큰 모두 재발급 후 쿠키 업데이트
            if (result.isExpired()) {
                JwtConfig.TokenResponse newTokenPair = jwt.createTokenPair(email);
                ResponseCookie newCookie = jwt.createRefreshTokenCookie(newTokenPair.refreshToken());

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                        .body(Map.of(
                                "accessToken", newTokenPair.accessToken(),
                                "message", "Refresh Token이 만료되어 전체 토큰이 재발급되었습니다."
                        ));
            }

            // 2) Refresh Token이 만료되지 않고 유효한 경우 -> Access Token만 재발급
            String newAccessToken = jwt.createAccessToken(email);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

        } catch (Exception e) {
            // 서명이 조작되었거나 올바르지 않은 타입일 경우 쿠키 삭제 후 에러 반환
            ResponseCookie deleteCookie = jwt.deleteToken();
            return ResponseEntity.badRequest()
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public String exitNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return "닉네임을 입력해주세요";
        }
        if (repo.existsByNickname(nickname)){
            return "이미 있는 닉네임입니다.";
        }
        return "사용가능한 닉네임입니다.";
    }

    public String exitEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "이메일을 입력해주세요";
        }
        if (repo.existsByEmail(email)){
            return "이미 존재하는 이메일입니다.";
        }
        return "사용가능한 이메일입니다.";
    }

    private SimpleMailMessage createEmailForm(String toEmail,
                                              String title,
                                              String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }



    public ResponseEntity emailVerficationConfirm(UserRequest request) {
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

    public void sendCodeToEmail(String email) {

        // 확인코드 담긴 이메일 발송
        String title = "Waylog 이메일 인증 번호";

        String resivedCode = "여기에 확인코드를 입력";

        String content = "<html>"
                + "<body>"
                + "<h1>ImgForest 인증 코드: " + resivedCode + "</h1>"
                + "<p>해당 코드를 홈페이지에 입력하세요.</p>"
                + "<footer style='color: grey; font-size: small;'>"
                + "<p>※본 메일은 자동응답 메일이므로 본 메일에 회신하지 마시기 바랍니다.</p>"
                + "</footer>"
                + "</body>"
                + "</html>";
        try {
            System.out.println("mailid : " + mailid);
            System.out.println("mailpw : " + mailpw);

            emailSender.send(createEmailForm(email, title, content));
        } catch (Exception e) {
            e.printStackTrace(); // 또는 로거를 사용하여 상세한 예외 정보 로깅
            throw new RuntimeException("Unable to send email in sendCodeToEmail", e); // 원인 예외를 포함시키기
        }
    }
    }

