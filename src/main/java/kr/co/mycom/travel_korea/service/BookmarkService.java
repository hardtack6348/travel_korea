package kr.co.mycom.travel_korea.service;

import jakarta.transaction.Transactional;
import kr.co.mycom.travel_korea.entity.BookmarkEntity;
import kr.co.mycom.travel_korea.entity.BookmarkId;
import kr.co.mycom.travel_korea.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    public  void drop(Long id) {
        bookmarkRepository.deleteByContentId(id);
    }


    public List<BookmarkEntity> getBookmarks() {
        return bookmarkRepository.findAll();
    }

    public BookmarkEntity add(BookmarkEntity entity) {
        System.out.println("addBookmark : " + entity);
        return bookmarkRepository.save(entity);
    }


    // 컨텐츠 ID로찾기
    public BookmarkEntity getBookmark(Long id, Long uid) {
        // 해당 ID로 저장소에서 특정 데이터를 꺼내오기
        BookmarkId bookmarkId = new BookmarkId(id, uid);
        return bookmarkRepository.findById(bookmarkId).get();
    }
}
