package kr.co.mycom.travel_korea.controller;

import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "amazonWebServeramazonWebServer")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/themes")
public class ThemeController {
    private final ThemeRepository themeRepository;
    @GetMapping()
    public List<UserEntity>  findAll() {
        return themeRepository.findAll();
    }

}
