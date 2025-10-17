package com.unicksbyte.inkspire.dto;

import com.unicksbyte.inkspire.entity.UserEntity;
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
public class PostRequest {

    private String title;

    private String content;

    private List<String> tags;

    private String category;
}
