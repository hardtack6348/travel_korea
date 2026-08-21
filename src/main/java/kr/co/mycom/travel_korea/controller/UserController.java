package kr.co.mycom.travel_korea.controller;

import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "amazonWebServeramazonWebServer")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService service;
    @GetMapping()
    public List<UserEntity> getUsers() {
        return service.findAll();
    }
}

