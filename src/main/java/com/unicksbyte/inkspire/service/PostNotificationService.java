package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.kafkamodel.NotificationData;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PostNotificationService {


    private EmailService emailService;

    private KafkaTemplate<String, NotificationData> kafkaTemplate;


    public void sendNewPostNotification(String authorName, String postTitle, List<String> followerEmails) {

        for (String email : followerEmails) {
            NotificationData payloadData = NotificationData.builder()
                    .author(authorName)
                    .postTitle(postTitle)
                    .followerEmail(email)
                    .build();

            try{
                kafkaTemplate.send("notify_email_to_followers", payloadData.getFollowerEmail(), payloadData);
            }

            //kafka fallback in case of kafka failure

            catch (Exception e)
            {
                String followerEmail = email;
                String subject = "Exciting Update: New Post from " + authorName;
                String body = "Hello, We’re thrilled to let you know that your favorite creator, *" + authorName + "*, has just published a new post titled:"
                        + postTitle + "Be the first to check it out, engage, and share your thoughts! Log in to Inkspire now to join the conversation and explore more inspiring content."
                        + "Keep creating and staying inspired,"
                        + "The Inkspire Team";

                emailService.sendEmail(followerEmail,subject,body);
            }

        }
    }
}
