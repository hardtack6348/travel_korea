package kr.co.mycom.travel_korea.controller;

import kr.co.mycom.travel_korea.entity.BookmarkEntity;
import kr.co.mycom.travel_korea.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("/api/v1/bookmarks")
    public List<BookmarkEntity>  getBookmarks() {
        return bookmarkService.getBookmarks();
    }
}
