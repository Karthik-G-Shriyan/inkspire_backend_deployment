package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.dto.CommentRequest;
import com.unicksbyte.inkspire.dto.CommentResponse;
import com.unicksbyte.inkspire.entity.UserEntity;
import com.unicksbyte.inkspire.kafkamodel.CommentModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentProducerService {

    @Autowired
    private KafkaTemplate<String, CommentModel> kafkaTemplate;

    private final UserService userService;

    private final CommentService commentService;


    public void sendCommentToKafka(String postPublicId, CommentRequest request) {

        UserEntity user = userService.findByUserId(); // currently logged-in user

        CommentModel payload = CommentModel.builder()
                .commenterId(user.getPublicId())
                .postId(postPublicId)
                .content(request.getContent())
                .build();

        try{
            kafkaTemplate.send("new_comments", payload.getPostId(), payload);
        }

        //kafka fallback in case of failure of kafka layer

        catch(Exception e)
        {
            CommentResponse commentResponse = commentService.addComment(postPublicId, request);
        }
    }
}
