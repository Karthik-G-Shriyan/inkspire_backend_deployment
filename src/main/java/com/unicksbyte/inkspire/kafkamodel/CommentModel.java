package com.unicksbyte.inkspire.kafkamodel;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentModel {

    private String postId;

    private String commenterId;

    private String content;
}
