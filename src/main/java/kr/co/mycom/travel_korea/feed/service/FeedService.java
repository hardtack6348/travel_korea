package kr.co.mycom.travel_korea.feed.service;

import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.feed.domain.*;
import kr.co.mycom.travel_korea.feed.dto.FeedCreateRequest;
import kr.co.mycom.travel_korea.feed.dto.FeedPageResponse;
import kr.co.mycom.travel_korea.feed.dto.FeedPostResponse;
import kr.co.mycom.travel_korea.feed.dto.FeedUpdateRequest;
import kr.co.mycom.travel_korea.feed.repository.FeedBookMarkRepository;
import kr.co.mycom.travel_korea.feed.repository.FeedLikeRepository;
import kr.co.mycom.travel_korea.feed.repository.FeedPostRepository;
import kr.co.mycom.travel_korea.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedPostRepository feedPostRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedBookMarkRepository feedBookMarkRepository;
    private final UserRepository userRepository;

    @Transactional
    public FeedPostResponse create(String loginEmail, FeedCreateRequest request) {
        UserEntity author = findUser(loginEmail);

        FeedPost post = new FeedPost(
                author,
                request.content().trim(),
                request.locationName(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.tourContentId(),
                request.tourContentTypeId(),
                normalizeVisibility(request.visibility())
        );

        post.replaceTags(request.tags());
        post.replacePhotos(request.imageUrls());

        FeedPost savedPost = feedPostRepository.save(post);

        return FeedPostResponse.from(savedPost, false, false);
    }

    public FeedPageResponse getFeed(String loginEmail, int page, int size) {
        /*
         * 프론트엔드는 1페이지부터 사용하지만 Spring Data는 0페이지부터 사용합니다.
         */
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = Math.min(Math.max(size, 1), 30);

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<FeedPost> postPage = feedPostRepository.findByVisibility("PUBLIC", pageable);

        List<Long> postIds = postPage.getContent().stream()
                .map(FeedPost::getId).toList();

        Set<Long> likedPostIds = new HashSet<>();
        Set<Long> bookmarkedPostIds = new HashSet<>();

        /*
         * 비로그인 사용자도 공개 피드를 조회할 수 있습니다.
         * 로그인 이메일이 있을 때만 개인별 좋아요·저장 여부를 조회합니다.
         */
        if (loginEmail != null && !loginEmail.isBlank() && !postIds.isEmpty()) {
            UserEntity user = findUser(loginEmail);

            feedLikeRepository.findByUser_IdAndFeedPost_IdIn(user.getId(), postIds)
                    .forEach(like -> likedPostIds.add(like.getFeedPost().getId()));

            feedBookMarkRepository.findByUser_IdAndFeedPost_IdIn(user.getId(), postIds)
                    .forEach(bookmark -> bookmarkedPostIds.add(bookmark.getFeedPost().getId()));
        }

        List<FeedPostResponse> responses = postPage.getContent().stream()
                .map(post -> FeedPostResponse.from(
                        post,
                        likedPostIds.contains(post.getId()),
                        bookmarkedPostIds.contains(post.getId())
                )).toList();

        return FeedPageResponse.from(postPage, responses);
    }

    public FeedPostResponse getOne(Long postId, String loginEmail) {
        FeedPost post = findPost(postId);

        boolean liked = false;
        boolean bookmarked = false;

        if (loginEmail != null && !loginEmail.isBlank()) {
            UserEntity user = findUser(loginEmail);

            liked = feedLikeRepository.existsByFeedPost_IdAndUser_Id(postId, user.getId());
            bookmarked = feedBookMarkRepository.existsByFeedPost_IdAndUser_Id(postId, user.getId());
        }

        return FeedPostResponse.from(post, liked, bookmarked);
    }

    @Transactional
    public FeedPostResponse update(Long postId, String loginEmail, FeedUpdateRequest request) {
        FeedPost post = findPost(postId);
        validateAuthor(post, loginEmail);

        post.update(
                request.content().trim(),
                request.locationName(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.tourContentId(),
                request.tourContentTypeId(),
                normalizeVisibility(request.visibility())
        );

        post.replaceTags(request.tags());
        post.replacePhotos(request.imageUrls());

        return FeedPostResponse.from(post, false, false);
    }

    @Transactional
    public void delete(Long postId, String loginEmail) {
        FeedPost post = findPost(postId);
        validateAuthor(post, loginEmail);

        /*
         * 사진, 태그, 좋아요, 북마크는 FK의 ON DELETE CASCADE 또는
         * JPA 관계 설정에 따라 함께 삭제됩니다.
         */
        feedPostRepository.delete(post);
    }


    @Transactional
    public boolean toggleLike(Long postId, String loginEmail) {
        UserEntity user = findUser(loginEmail);
        FeedPost post = findPost(postId);

        FeedLikeId likeId = new FeedLikeId(post.getId(), user.getId());

        if (feedLikeRepository.existsById(likeId)) {
            feedLikeRepository.deleteById(likeId);
            post.decreaseLikeCount();
            return false;
        }

        feedLikeRepository.save(new FeedLike(post, user));
        post.increaseLikeCount();
        return true;
    }

    @Transactional
    public boolean toggleBookmark(Long postId, String loginEmail) {
        UserEntity user = findUser(loginEmail);
        FeedPost post = findPost(postId);

        FeedBookMarkId bookmarkId = new FeedBookMarkId(post.getId(), user.getId());

        if (feedBookMarkRepository.existsById(bookmarkId)) {
            feedBookMarkRepository.deleteById(bookmarkId);
            return false;
        }

        feedBookMarkRepository.save(new FeedBookMark(post, user));
        return true;
    }

    private UserEntity findUser(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
    }

    private FeedPost findPost(Long postId) {
        return feedPostRepository.findWithDetailsById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    private void validateAuthor(FeedPost post, String loginEmail) {
        if (!post.getAuthor().getEmail().equals(loginEmail)) {
            throw new IllegalArgumentException("게시글 작성자만 수정하거나 삭제할 수 있습니다.");
        }
    }

    private String normalizeVisibility(String visibility) {
        if (visibility == null || visibility.isBlank()) {
            return "PUBLIC";
        }

        String normalized = visibility.toUpperCase();

        if (!normalized.equals("PUBLIC") && !normalized.equals("PRIVATE")) {
            throw new IllegalArgumentException("공개 범위는 PUBLIC 또는 PRIVATE만 가능합니다.");
        }

        return normalized;
    }
}
