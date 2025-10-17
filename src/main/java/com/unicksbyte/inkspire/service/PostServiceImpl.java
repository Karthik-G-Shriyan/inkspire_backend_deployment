package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.PostPreviewResponse;
import com.unicksbyte.inkspire.dto.PostRequest;
import com.unicksbyte.inkspire.dto.PostResponse;
import com.unicksbyte.inkspire.entity.PostEntity;
import com.unicksbyte.inkspire.entity.UnsafePostEntity;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.exception.ResourceNotFoundException;
import com.unicksbyte.inkspire.exception.UnauthorizedActionException;
import com.unicksbyte.inkspire.repository.PostRepository;
import com.unicksbyte.inkspire.repository.UnsafePostRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final UnsafePostRepository unsafePostRepository;

    private final ContentModerationService contentModerationService;

    private final UserService userService;


    private final PostNotificationService postNotificationService;

    @Override
    @CachePut(value = "posts", key = "#result.publicId")
    public PostResponse createPost(PostRequest request) {

        PostEntity newPost = convertToEntity(request);

        UserEntity loggedInUser = userService.findByUserId();

        newPost.setUser(loggedInUser);
        newPost.setUpdatedAt(LocalDateTime.now());

        newPost = postRepository.save(newPost);

        //notification to all followers
        Set<UserEntity> followers = loggedInUser.getFollowers();
        List<String> followerEmails = followers.stream()
                .map(UserEntity::getEmail)
                .toList();

        // Send email
       postNotificationService.sendNewPostNotification(loggedInUser.getUserName(), newPost.getTitle(), followerEmails);

        PostResponse response =  convertToResponse(newPost);


        // 2️⃣ Send content to GPT for moderation
        Map<String, Object> moderationResult = contentModerationService.moderatePost(newPost.getContent());
        String status = (String) moderationResult.get("status");

        // 3️⃣ If unsafe, store in UnsafePostEntity
        if ("NOT_OK".equals(status)) {
            UnsafePostEntity unsafe = UnsafePostEntity.builder()
                    .post(newPost)
                    .reason((String) moderationResult.get("reason"))
                    .keywords( (String) moderationResult.get("keywords"))
                    .build();

            unsafePostRepository.save(unsafe);
        }


        return  response;

    }

    @Override
    @CachePut(value = "posts", key = "#postId")
    public PostResponse updatePost(String postId, PostRequest request) {

        // Fetch the existing post
        PostEntity post = postRepository.findByPublicId(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + postId + " not found"));

        // Get the currently logged-in user
        UserEntity loggedInUser = userService.findByUserId();

        // Check if the logged-in user is the author
        if (!post.getUser().getPublicId().equals(loggedInUser.getPublicId())) {
            throw new UnauthorizedActionException("You are not authorized to update this post");
        }

        // Update only the fields you want to change
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setTags(request.getTags());
        post.setCategory(request.getCategory());
        post.setUpdatedAt(LocalDateTime.now());

        // Save the updated post
        PostEntity updatedPost = postRepository.save(post);

        // Convert to response DTO
        PostResponse response = convertToResponse(updatedPost);

        return response;


    }


    @Override
    @CacheEvict(value = "posts", key = "#postId")
    public void deletePost(String postId) {

        Optional<PostEntity> postOptional = postRepository.findByPublicId(postId);

        if (postOptional.isEmpty()) {

            throw new ResourceNotFoundException("Post with id " + postId + " not found");
        }

        PostEntity post = postOptional.get();

        // Get the currently logged-in user
        UserEntity loggedInUser = userService.findByUserId();

        // Check if the logged-in user is the author
        if (!Objects.equals(post.getUser().getPublicId(), loggedInUser.getPublicId())) {

            throw new UnauthorizedActionException("You are not authorized to delete this post");
        }

        // Delete the post
        postRepository.delete(post);
    }

    @Override
    public Page<PostPreviewResponse> getAllPosts(int page, int size) {

        // For DB pagination (page >= 0)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostEntity> postsPage = postRepository.findAll(pageable);

        Page<PostPreviewResponse> responsePage = postsPage.map(this::convertToPreviewResponse);

        return responsePage;
    }



    @Override
    @Cacheable(value = "posts", key = "#postId")
    public PostResponse getPostById(String postId) {
        PostEntity post = postRepository.findByPublicId(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + postId + " not found"));
        return convertToResponse(post);
    }

    @Override
    public List<PostPreviewResponse> getUserPosts(String userId) {
        List<PostEntity> posts = postRepository.findByUser_PublicIdOrderByCreatedAtDesc(userId);

        return posts.stream()
                .map(this::convertToPreviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostPreviewResponse> searchPosts(String query, String category) {

            return postRepository.searchPostsNative(query, category)
                    .stream()
                    .map(this::convertToPreviewResponse)
                    .toList();

    }


    private PostEntity convertToEntity(PostRequest request ){
       return PostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .tags(request.getTags())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private PostResponse convertToResponse(PostEntity newPost) {

       return PostResponse.builder()
                .publicId(newPost.getPublicId())
                .title(newPost.getTitle())
                .content(newPost.getContent())
                .authorId(newPost.getUser().getPublicId())
               .authorName(newPost.getUser().getUserName())
                .category(newPost.getCategory())
                .updatedAt(newPost.getUpdatedAt())
                .tags(newPost.getTags())
                .build();
    }


    private PostPreviewResponse convertToPreviewResponse(PostEntity postEntity) {
        return PostPreviewResponse.builder()
                .publicId(postEntity.getPublicId())
                .title(postEntity.getTitle())
                // first 200 characters as preview
                .preview(getPreview(postEntity.getContent()))
                .authorId(postEntity.getUser().getPublicId())
                .authorName(postEntity.getUser().getUserName())
                .category(postEntity.getCategory())
                .updatedAt(postEntity.getUpdatedAt())
                .tags(postEntity.getTags())
                .build();


    }

    private String getPreview(String content) {
        if (content == null) return "";
        return content.length() > 200 ? content.substring(0, 200) + "..." : content;
    }

}
