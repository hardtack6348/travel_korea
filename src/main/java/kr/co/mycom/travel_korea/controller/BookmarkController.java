package kr.co.mycom.travel_korea.controller;

import kr.co.mycom.travel_korea.entity.BookmarkEntity;
import kr.co.mycom.travel_korea.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:5173")
//@RequiredArgsConstructor
@RestController
public class BookmarkController {
    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
        System.out.println("BookmarkController  생성자 호출중..................................");
    }

    @GetMapping("/api/v1/bookmarks")
    public List<BookmarkEntity>  getBookmarks() {
        System.out.println("getBookmarks =========================> ");
        return bookmarkService.getBookmarks();
    }

    @PostMapping("/api/v1/contents/{id}/bookmark")
    public BookmarkEntity getBookmarkById(@PathVariable Long id, @RequestBody BookmarkEntity  bookmark) {
        System.out.println("getBookmarkById : " + bookmark);
            return bookmarkService.add(bookmark);
    }
}
