package com.unicksbyte.inkspire.controller;

import com.unicksbyte.inkspire.dto.PostPreviewResponse;
import com.unicksbyte.inkspire.dto.PostRequest;
import com.unicksbyte.inkspire.dto.PostResponse;
import com.unicksbyte.inkspire.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {


        private final PostService postService;


    @GetMapping
    public Page<PostPreviewResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return postService.getAllPosts(page, size);
    }

    @GetMapping("/user/{userId}")
    public List<PostPreviewResponse> getUserPosts(@PathVariable String userId) {
        return postService.getUserPosts(userId);
    }


    @GetMapping("/{id}")
    public PostResponse getPostById(@PathVariable String id) {
        PostResponse postById = postService.getPostById(id);
        return  postById;
    }


        @PostMapping("/create")
        public PostResponse createPost(@RequestBody PostRequest request) {
            return postService.createPost(request);
        }


        @PutMapping("/update/{id}")
        public PostResponse updatePost(@PathVariable String id, @RequestBody PostRequest request) {
            return postService.updatePost(id, request);
        }


    @GetMapping("/search")
    public List<PostPreviewResponse> searchPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category

    ) {
       return postService.searchPosts(search, category );

    }


        @DeleteMapping("/delete/{id}")
        public void deletePost(@PathVariable String id) {
            postService.deletePost(id);
        }


}
