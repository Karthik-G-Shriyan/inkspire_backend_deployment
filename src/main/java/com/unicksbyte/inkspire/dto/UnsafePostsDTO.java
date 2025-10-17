package com.unicksbyte.inkspire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnsafePostsDTO {
    private String publicId;   // publicId of the original post
    private String title;
    private String category;
    private String tags;
    // comma-separated tags
    private String reason;
    // GPT moderation reason
    private String keywords;
    // comma-separated keywords
    private LocalDateTime flaggedAt;
}
