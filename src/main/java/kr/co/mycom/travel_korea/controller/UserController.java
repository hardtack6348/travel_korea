package kr.co.mycom.travel_korea.controller;

import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "amazonWebServer")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService service;

    @GetMapping("/{id}")
    public UserEntity getUserInfo(@PathVariable Long id) {
        UserEntity user = service.findUserInfo(id);
        return user;

    }
}

