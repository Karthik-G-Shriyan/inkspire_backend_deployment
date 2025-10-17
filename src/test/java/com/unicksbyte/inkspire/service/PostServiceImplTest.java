package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.PostRequest;
import com.unicksbyte.inkspire.dto.PostResponse;
import com.unicksbyte.inkspire.entity.PostEntity;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.repository.PostRepository;
import com.unicksbyte.inkspire.repository.UnsafePostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UnsafePostRepository unsafePostRepository;

    @Mock
    ContentModerationService contentModerationService;

    @Mock
    UserService userService;

    @Mock
    PostNotificationService postNotificationService;


    @InjectMocks
    PostServiceImpl postService;

    @Test
    void createPost_shouldSaveSuccessfully() {
        // mock data
        UserEntity user = new UserEntity();
        user.setPublicId("u1");
        user.setUserName("John");

        PostEntity savedPost = PostEntity.builder()
                .publicId("p1")
                .title("Hello World")
                .content("This is safe")
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PostRequest request = new PostRequest();
        request.setTitle("Hello World");
        request.setContent("This is safe");

        // define how mocks should behave
        when(userService.findByUserId()).thenReturn(user);
        when(postRepository.save(any(PostEntity.class))).thenReturn(savedPost);
        when(contentModerationService.moderatePost(anyString())).thenReturn(Map.of("status", "OK"));

        // call the real method
        PostResponse response = postService.createPost(request);

        // check result
        assertNotNull(response);
        assertEquals("Hello World", response.getTitle());

        // verify interactions
        verify(postRepository).save(any(PostEntity.class));
        verify(unsafePostRepository, never()).save(any());
    }
}

