package kr.co.mycom.travel_korea.controller;

import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.request.UserRequest;
import kr.co.mycom.travel_korea.service.LoginService;
import lombok.RequiredArgsConstructor;
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
    public UserEntity login(@RequestBody UserRequest request) {
        return service.login(request);
    }

    @PostMapping("/logout")
    public UserEntity logout() {
        return service.logout();
    }

    @PostMapping("/refresh")
    public UserEntity refresh(@RequestBody UserRequest request) {
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
