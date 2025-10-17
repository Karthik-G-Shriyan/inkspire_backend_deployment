package com.unicksbyte.inkspire.service;


import com.unicksbyte.inkspire.dto.CommentResponse;
import com.unicksbyte.inkspire.dto.CommentRequest;

import java.util.List;

public interface CommentService {

    // Add a comment to a post
    CommentResponse addComment(String postId, CommentRequest request);

    // Get all comments for a post
    List<CommentResponse> getCommentsByPost(String postId);

    // Get all comments written by a user
    List<CommentResponse> getCommentsByUser(String userPublicId);

    // Delete a comment by its ID
    void deleteComment(String commentId);
}

