package com.unicksbyte.inkspire.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Service
public class ContentModerationService {

    private final ChatClient chatClient;

    private final ObjectMapper objectMapper;

    @Autowired
    public ContentModerationService(ChatClient chatClient, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> moderatePost(String postContent) {
        String prompt = """
            You are a content moderation assistant. 
            Check the following post for adult, sexual, violent, offensive, or dangerous content. 
            Respond in JSON format:
            {
              "status": "OK" or "NOT_OK",
              "reason": "short explanation",
              "keywords": "keyword1,keyword2,keyword3"
            }

            Post content:
            "%s"
            """.formatted(postContent);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content()
                .trim()
                .replaceAll("(?s)```json|```", "");

        try {
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                    "status", "UNKNOWN",
                    "reason", "Failed to parse GPT response",
                    "keywords", new String[]{}
            );
        }
    }
}

