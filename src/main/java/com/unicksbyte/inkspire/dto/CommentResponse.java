package com.unicksbyte.inkspire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {


    private String commentId;

    private String writerName;

    private String writerPublicId;

    private String content;

    private String postId;

    private LocalDateTime createdAt;
}
