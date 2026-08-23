package kr.co.mycom.travel_korea.controller;

import com.nimbusds.jose.JOSEException;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.mycom.travel_korea.config.JwtConfig;
import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.request.UserRequest;
import kr.co.mycom.travel_korea.service.LoginService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "https://api.waylog.com/")
@CrossOrigin(origins = "http://localhost:8080/")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final LoginService service;

    @PostMapping("/signup")
    public UserEntity signup(@RequestBody UserRequest request) {
        return service.signup(request);
    }

    @PostMapping("/login")
    public JwtConfig.TokenResponse login(@RequestBody UserRequest request) throws JOSEException {
        return service.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
          ResponseCookie cookie = service.logout();
        return ResponseEntity.ok()
                 .header("Set-Cookie", cookie.toString())
                .body("로그아웃이 정상적으로 처리되었습니다.");
    }

    @PostMapping("/refresh")
    public JwtConfig.TokenResponse refresh(@RequestBody UserRequest request) {
        return service.refresh(request);
    }

    @PostMapping("/email-verfications")
    public UserEntity emailVerfications(@RequestBody UserRequest request) {
        return service.emailVerfication(request);
    }

    @PostMapping("/email-verfications/confirm")
    public UserEntity emailVerficationsConfirm(@RequestBody UserRequest request) {
        return service.emailVerficationConfirm(request);
    }

    @PostMapping("/password-reset-requests")
    public UserEntity passwordResetRequests(@RequestBody UserRequest request) {
        return service.passwordResetRequest(request);
    }

    @PutMapping("/password")
    public UserEntity changePassword(@RequestBody UserRequest request) {
        return service.changePassword(request);
    }
}
