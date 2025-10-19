package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.CommentRequest;
import com.unicksbyte.inkspire.dto.CommentResponse;
import com.unicksbyte.inkspire.entity.CommentEntity;
import com.unicksbyte.inkspire.entity.PostEntity;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.exception.ResourceNotFoundException;
import com.unicksbyte.inkspire.kafkamodel.CommentModel;
import com.unicksbyte.inkspire.repository.CommentRepository;
import com.unicksbyte.inkspire.repository.PostRepository;
import com.unicksbyte.inkspire.service.CommentService;
import com.unicksbyte.inkspire.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserService userService; // to get currently logged-in user

    @Override
    public CommentResponse addComment(String postId, CommentRequest request) {
        PostEntity post = postRepository.findByPublicId(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        UserEntity user = userService.findByUserId(); // currently logged-in user

        CommentEntity comment = convertToEntity(request);

        comment.setUser(user);
        comment.setPost(post);

        CommentEntity saved = commentRepository.save(comment);
        return convertToResponse(saved);
    }

    @KafkaListener(topics = "new_comments", groupId = "inkspire-comment-service")
    public void  commentConsumerKafka(CommentModel commentModel) {
        PostEntity post = postRepository.findByPublicId(commentModel.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));


        // Fetch the user who commented
        UserEntity user = userService.findByPublicId(commentModel.getCommenterId());

        // Convert Kafka model to entity
        CommentEntity comment = new CommentEntity();
        comment.setContent(commentModel.getContent());
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        // Save to DB
        commentRepository.save(comment);
    }


    @Override
    public List<CommentResponse> getCommentsByPost(String postId) {
        PostEntity post = postRepository.findByPublicId(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return commentRepository.findByPost(post)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByUser(String userPublicId) {

        //logged-in user
        UserEntity user = userService.findByUserId();

        return commentRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(String commentId) {
        CommentEntity comment = commentRepository.findByPublicId(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Optional: Check if the logged-in user is the author of the comment
        UserEntity loggedInUser = userService.findByUserId();
        if (!comment.getUser().getPublicId().equals(loggedInUser.getPublicId())) {
            throw new IllegalStateException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentEntity convertToEntity(CommentRequest request) {
        return CommentEntity.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Convert CommentEntity to CommentResponse DTO
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
}
