package kr.co.mycom.travel_korea.controller;

import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.repository.UserRepository;
import kr.co.mycom.travel_korea.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//@CrossOrigin(origins = "https://api.waylog.com/")
@CrossOrigin(origins = "http://localhost:5173/")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService service;

    @GetMapping("users/check-email")
    public ResponseEntity checkEmail(@RequestParam("email") String email) {
        if (service.existEmail(email)) {
            Map<String, Object> response = new HashMap<>();
            response.put("available", false);
            ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            System.out.println("존재하지 않음");
        }
        return ResponseEntity.ok(service.findUserInfo(email));
    }

    @GetMapping("users/check-nickname")
    public ResponseEntity checkNickname(@RequestParam("nickname") String nickname) {
        if (service.existNickname(nickname)){
            Map<String, Object> response = new HashMap<>();
            response.put("available", false);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return new ResponseEntity<>(nickname, HttpStatus.OK);
    }


}

