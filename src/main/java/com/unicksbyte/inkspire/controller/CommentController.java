package com.unicksbyte.inkspire.controller;

import com.unicksbyte.inkspire.dto.CommentRequest;
import com.unicksbyte.inkspire.dto.CommentResponse;
import com.unicksbyte.inkspire.service.CommentProducerService;
import com.unicksbyte.inkspire.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final CommentProducerService commentProducerService;

    // Add a comment to a post
    @PostMapping("/{postPublicId}")
    public ResponseEntity<String> addComment(
            @PathVariable String postPublicId,
            @RequestBody CommentRequest request
    ) {
        commentProducerService.sendCommentToKafka(postPublicId, request);

        // Return immediate response
        return ResponseEntity.accepted()
                .body("Comment is being processed and will appear shortly.");
    }


    // Get all comments for a post
    @GetMapping("/{postPublicId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable String postPublicId) {
        List<CommentResponse> comments = commentService.getCommentsByPost(postPublicId);
        return ResponseEntity.ok(comments);
    }

    // Delete a comment by its publicId
    @DeleteMapping("/delete/{commentPublicId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentPublicId) {
        commentService.deleteComment(commentPublicId);
        return ResponseEntity.noContent().build();
    }
}

