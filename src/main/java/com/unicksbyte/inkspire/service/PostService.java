package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.PostPreviewResponse;
import com.unicksbyte.inkspire.dto.PostRequest;
import com.unicksbyte.inkspire.dto.PostResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request);

   PostResponse updatePost(String postId, PostRequest request);

    void deletePost(String postId);

    Page<PostPreviewResponse> getAllPosts(int page, int size);

    PostResponse getPostById(String postId);

    public List<PostPreviewResponse> getUserPosts(String userId);

     List<PostPreviewResponse> searchPosts(String query, String category);

}
