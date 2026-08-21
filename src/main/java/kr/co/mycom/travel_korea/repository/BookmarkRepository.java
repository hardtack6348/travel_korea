package kr.co.mycom.travel_korea.repository;

import kr.co.mycom.travel_korea.entity.BookmarkEntity;
import kr.co.mycom.travel_korea.entity.BookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, BookmarkId> {

    void deleteByContentId(Long id);
}
