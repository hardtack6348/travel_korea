package kr.co.mycom.travel_korea.controller;

import com.nimbusds.jose.JOSEException;

import kr.co.mycom.travel_korea.config.JwtConfig;
import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.request.MailRequest;
import kr.co.mycom.travel_korea.request.UserRequest;
import kr.co.mycom.travel_korea.service.AuthService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "https://api.waylog.com/")
@CrossOrigin(origins = "http://localhost:8080/")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;

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
    public ResponseEntity<?> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        return service.refreshToken(refreshToken);
    }

    @PostMapping("/email-verfications")
    public void sendMessage(@RequestParam("email") String email) {
        service.sendCodeToEmail(email);
    }

    @GetMapping("/email-verfications/confirm")
    public ResponseEntity verificationEmail(@RequestParam("email") String email,
                                            @RequestParam("code") String authCode) {
//        String response = service.emailVerficationConfirm(email, authCode);
//        return new ResponseEntity<>(new MailRequest(response), HttpStatus.OK);
        return null;
    }

    @PostMapping("/password-reset-requests")
    public UserEntity passwordResetRequests(@RequestBody UserRequest request) {
        return service.passwordResetRequest(request);
    }

    @PutMapping("/password")
    public UserEntity changePassword(@RequestBody UserRequest request) {
        return service.changePassword(request);
    }

    @PostMapping("/findNickname")
    public String findNickname(@RequestBody UserRequest request) {
        return service.exitNickname(request.getNickname());
    }
}
