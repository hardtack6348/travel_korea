package kr.co.mycom.travel_korea.service;

import kr.co.mycom.travel_korea.entity.BookmarkEntity;
import kr.co.mycom.travel_korea.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;


    public List<BookmarkEntity> getBookmarks() {
        return bookmarkRepository.findAll();
    }

    public BookmarkEntity add(BookmarkEntity entity) {
        System.out.println("addBookmark : " + entity);
        return bookmarkRepository.save(entity);
    }
}
