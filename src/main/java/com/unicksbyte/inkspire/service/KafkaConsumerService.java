package com.unicksbyte.inkspire.service;

import com.unicksbyte.inkspire.kafkamodel.NotificationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    private EmailService emailService;

    //@KafkaListener(topics = "notify_email_to_followers"  ,groupId = "inkspire-email-service")
    public void consumeFromKafka(NotificationData notificationData){

        String followerEmail = notificationData.getFollowerEmail();
        String subject = "New post from " + notificationData.getAuthor();
        String body = "Hello,\n\n"
                + "Your favorite creator *" + notificationData.getAuthor() + "* has just published a new post titled:\n\n"
                + "\"" + notificationData.getPostTitle() + "\"\n\n"
                + "Don’t miss out—log in to Inkspire now and join the conversation.\n\n"
                + "Stay inspired,\n"
                + "The Inkspire Team";
        emailService.sendEmail(followerEmail,subject,body);

    }


}
