package com.unicksbyte.inkspire.kafkamodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationData {

    private String author;

    private String followerEmail;

    private String postTitle;

}
