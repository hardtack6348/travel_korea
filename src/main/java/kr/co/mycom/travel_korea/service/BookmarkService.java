package kr.co.mycom.travel_korea.service;

import kr.co.mycom.travel_korea.entity.BookmarkEntity;
import kr.co.mycom.travel_korea.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    public List<BookmarkEntity> getBookmarks() {
        return bookmarkRepository.findAll();
    }

}
