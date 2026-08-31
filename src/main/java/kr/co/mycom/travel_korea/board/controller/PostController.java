package kr.co.mycom.travel_korea.board.controller;

import kr.co.mycom.travel_korea.board.dto.*;
import kr.co.mycom.travel_korea.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")

public class PostController {
    private final PostService postService;
    // localhost:8080/api/posts?keyword=키보드
    @GetMapping
    public PageResponse<PostListResponse> list
    (@RequestParam(required = false)String keyword,
     @RequestParam(defaultValue = "0")int page,
     @RequestParam(defaultValue = "10") int size) {
        // keyword : 키보드 , page = 0 , size = 10
        return postService.list(keyword, page, size);
    }
    @GetMapping("/{id}")
    public PostDetailResponse get(@PathVariable Long id){
        return postService.get(id);
    }

    @GetMapping("/{id}/edit")
    public PostDetailResponse getForEdit(@PathVariable Long id){
        return postService.getWithoutIncreasingView(id);
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDetailResponse> create(
            @RequestPart("post") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.create(request, images));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostDetailResponse update(
            @PathVariable Long id,
            @RequestPart("post") PostUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return postService.update(id, request, images);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }



}
