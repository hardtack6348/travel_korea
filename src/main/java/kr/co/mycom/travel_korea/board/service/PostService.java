package kr.co.mycom.travel_korea.board.service;

import kr.co.mycom.travel_korea.board.dto.*;
import kr.co.mycom.travel_korea.board.entity.Post;
import kr.co.mycom.travel_korea.board.entity.PostImage;
import kr.co.mycom.travel_korea.board.repository.PostRepository;
import kr.co.mycom.travel_korea.board.storage.StorageService;
import kr.co.mycom.travel_korea.board.storage.StoredObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {
    private final StorageService storageService;
    private final PostRepository repo;

    public PageResponse<PostListResponse> list (String keyword, int page, int size){
        int safePage = Math.max(0, page);
        int safeSize = Math.min((Math.max(1,size)), 50);
        Pageable pageable = PageRequest.of(safePage,safeSize, Sort.by(Sort.Direction.DESC,"createdAt","id"));
        Page<Post> result =  repo.search(keyword,pageable);
        Page<PostListResponse> map =
            result.map(p -> new PostListResponse(
                p.getId(),
                p.getTitle(),
                p.getAuthor(),
                p.getViewCount(),
                0,
                p.getCreatedAt(),
                false
        ));
         return PageResponse.from(map);
    }

    public void delete(Long id) {
       Post post = findPost(id);
       List<String> keys = post.getImages().stream().map(PostImage::getObjectKey).toList();
       repo.delete(post);
       keys.forEach(key -> safeDelete(key));
    }

    public PostDetailResponse create(PostCreateRequest request, List<MultipartFile> images) {
        List<MultipartFile> safeImages = normalizeFiles(images);
        validateImageCount(safeImages.size());
        Post post = new Post(request.title(),request.content(),request.author());
        List<String> uploadedKeys = new ArrayList<>();
        try{
            for (MultipartFile image : safeImages) {
                StoredObject stored = storageService.upload(image);
                uploadedKeys.add(stored.objectKey());
                post.addImages(new PostImage(stored.objectKey(), stored.originalFilename(), stored.contentType(), stored.size()
                ));
            }
            Post saved = repo.save(post);
            return toDetail(saved);
        } catch(RuntimeException e){
            uploadedKeys.forEach(key->safeDelete(key));
            throw e;
        }
    }

    private void safeDelete(String key) {
        try{
            storageService.delete(key);
        } catch (RuntimeException e) {}
    }

    private PostDetailResponse toDetail(Post post) {
       List<PostImageResponse> imageResponses = post.getImages().stream().map(image -> new PostImageResponse(
               image.getId(),
               image.getObjectKey(),
               image.getOriginalFileName(),
               image.getContentType(),
               image.getSize(),
               storageService.createReadUrl(image.getObjectKey())
       )).toList();
        return new PostDetailResponse(post.getId(), post.getTitle(), post.getContent(),
                post.getAuthor(), post.getViewCount(), post.getCreatedAt(),
                post.getUpdatedAt(), imageResponses);
    }

    private List<MultipartFile> normalizeFiles(List<MultipartFile> files) {
        if(files == null || files.isEmpty()) return List.of();
        return files.stream().filter(f-> f!= null&& !f.isEmpty()).toList();
    }
    private void validateImageCount(int size) {

    }
    @Transactional
    public PostDetailResponse get(Long postId) {
        Post post = findPost(postId);
        post.increseViewCount();
        return toDetail(post);
    }

    public Post findPost(Long id) {
        return repo.findById(id).orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
    }
    public PostDetailResponse getWithoutIncreasingView(Long id) {
      return toDetail(findPost(id));
    }

    @Transactional
    public PostDetailResponse update(Long id, PostUpdateRequest request, List<MultipartFile> images) {
        Post post = findPost(id);
        Set<Long> removeIds = new HashSet<>(request.safeRemoveImageIds());
        //현재 가지고 있는 이미지중 삭제대상만 골라냄
        List<PostImage> removing = post.getImages().stream()
                .filter(image -> removeIds.contains(image.getId()))
                .toList();
        //게시물 1개에 여러장의 이미지 사용 여부
        int remaining = post.getImages().size()-removing.size();
        List<MultipartFile> safeNewImages = normalizeFiles(images);
        validateImageCount(remaining + safeNewImages.size());
        List<String> uploadKeys = new ArrayList<>();

        try{
            for (MultipartFile image : safeNewImages) {
                StoredObject stored = storageService.upload(image);
                uploadKeys.add(stored.objectKey());
                post.addImage(new PostImage(stored.objectKey(), stored.originalFilename(), stored.contentType(), stored.size()));
            }
            post.update(request.title(), request.content(), request.author());
            for (PostImage image : removing) {
                post.removeImage(image);
                safeDelete(image.getObjectKey());
            }
                return toDetail(post);
        }catch(RuntimeException e){
            uploadKeys.forEach(key->safeDelete(key));
            throw e;
        }
    }

}
