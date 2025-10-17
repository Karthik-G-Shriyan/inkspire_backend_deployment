package com.unicksbyte.inkspire.controller;

import com.unicksbyte.inkspire.dto.*;
import com.unicksbyte.inkspire.entity.CommentEntity;
import com.unicksbyte.inkspire.entity.PostEntity;
import com.unicksbyte.inkspire.entity.UnsafePostEntity;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.exception.ResourceNotFoundException;
import com.unicksbyte.inkspire.repository.CommentRepository;
import com.unicksbyte.inkspire.repository.PostRepository;
import com.unicksbyte.inkspire.repository.UnsafePostRepository;
import com.unicksbyte.inkspire.repository.UserRepository;
import com.unicksbyte.inkspire.service.AppUserDetailsService;
import com.unicksbyte.inkspire.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {


    private final AuthenticationManager authenticationManager;

    private final AppUserDetailsService userDetailsService;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final UnsafePostRepository unsafePostRepository;

    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());


        boolean isUser = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isUser) {
            throw new RuntimeException("Access denied: not an valid user.");
        }

        UserEntity userEntity = userDetailsService.findByEmail(request.getEmail());
        boolean emailVerified = userEntity.isEmailVerified();

        final String jwtToken = jwtUtils.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .email(request.getEmail())
                .token(jwtToken)
                .userName(userEntity.getUserName())
                .publicId(userEntity.getPublicId())
                .role(userEntity.getRole())
                .emailVerified(userEntity.isEmailVerified())
                .build();

    }

    @DeleteMapping("posts/delete/{id}")
    @CacheEvict(value = "posts", key = "#postId")
    public void deletePostByAdmin(@PathVariable String id) {

        Optional<PostEntity> postOptional = postRepository.findByPublicId(id);

        if (postOptional.isEmpty()) {

            throw new ResourceNotFoundException("Post with id " + id + " not found");
        }



        PostEntity post = postOptional.get();

        postRepository.delete(post);

    }

    @GetMapping("/comments")
    public List<CommentResponse> getAllComments(){

        return commentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("comments/delete/{commentId}")
    public void deleteMapping(@PathVariable String commentId){

       Optional<CommentEntity>  comment = commentRepository.findByPublicId(commentId);

       if(comment.isEmpty()){
           throw new ResourceNotFoundException("Comment with id " + commentId + " not found");

       }
        CommentEntity commentPresent = comment.get();

       commentRepository.delete(commentPresent);

    }

    @GetMapping("/all")
    public List<FollowResponse> getAllUsers(){
        List<UserEntity> all = userRepository.findAll();

        return all.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/unsafe-posts")
    public List<UnsafePostsDTO> getAllUnsafePosts() {
        List<UnsafePostEntity> unsafePosts = unsafePostRepository.findAllByOrderByFlaggedAtDesc();

        return unsafePosts.stream()
                .map(unsafe -> new UnsafePostsDTO(
                        unsafe.getPost().getPublicId(),
                        unsafe.getPost().getTitle(),
                        unsafe.getPost().getCategory(),
                        String.join(",", unsafe.getPost().getTags()),
                        unsafe.getReason(),
                        unsafe.getKeywords(),
                        unsafe.getFlaggedAt()
                ))
                .collect(Collectors.toList());
    }

    private CommentResponse convertToResponse(CommentEntity comment) {
        return CommentResponse.builder()
                .commentId(comment.getPublicId())
                .postId(comment.getPost().getPublicId())
                .writerName(comment.getUser().getUserName())
                .writerPublicId(comment.getUser().getPublicId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private FollowResponse convertToUserResponse(UserEntity registeredUser) {
        return FollowResponse.builder()
                .userName(registeredUser.getUserName())
                .publicId(registeredUser.getPublicId())
                .email(registeredUser.getEmail())
                .build();
    }

}

