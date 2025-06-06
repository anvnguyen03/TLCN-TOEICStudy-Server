package com.toeic.utils;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenAIUtils {
    
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;
    
    public OpenAIUtils(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
        this.objectMapper = new ObjectMapper();
    }
    
    public String generateResponse(String systemPrompt, Object data) {
        try {
            // Convert data to JSON string if it's not already a string
            String dataStr = data instanceof String ? (String) data : objectMapper.writeValueAsString(data);
            
            log.info("Calling OpenAI API with prompt: {}", systemPrompt);
            log.debug("Data being sent: {}", dataStr);
            
            // Create messages for the chat
            List<ChatMessage> messages = List.of(
                new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
                new ChatMessage(ChatMessageRole.USER.value(), dataStr)
            );
            
            // Create the chat completion request
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(0.7)
                .maxTokens(1000)
                .build();
            
            // Get the response
            String response = openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
            
            log.info("Successfully received response from OpenAI");
            return response;
            
        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            if (e.getCause() != null) {
                log.error("Caused by: {}", e.getCause().getMessage());
            }
            return "Unable to generate analysis at this time. Please try again later.";
        }
    }
} 