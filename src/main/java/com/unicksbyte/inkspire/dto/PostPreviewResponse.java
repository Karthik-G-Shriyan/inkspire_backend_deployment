package com.unicksbyte.inkspire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostPreviewResponse {


    private String publicId;

    private String title;

    private String preview;

    private String authorId;

    private String authorName;

    private LocalDateTime updatedAt;

    private List<String> tags;

    private String category;


}
