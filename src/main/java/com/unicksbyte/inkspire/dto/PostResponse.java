package com.unicksbyte.inkspire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {


    private String publicId;

    private String title;

    private String content;

    private String authorId;

    private String authorName;

    private LocalDateTime updatedAt;

    private List<String> tags;

    private String category;
}
