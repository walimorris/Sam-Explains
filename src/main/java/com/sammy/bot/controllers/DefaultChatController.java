package com.sammy.bot.controllers;

import com.sammy.bot.configurations.ApplicationPropertiesConfiguration;
import com.sammy.bot.services.SlackReaderImpl;
import com.slack.api.model.Message;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sammy/api/default")
public class DefaultChatController {
    private final ApplicationPropertiesConfiguration configuration;
    private final SlackReaderImpl slackReader;

    public DefaultChatController(ApplicationPropertiesConfiguration configuration, SlackReaderImpl slackReader) {
        this.configuration = configuration;
        this.slackReader = slackReader;
    }

    @GetMapping("/listOptions")
    public ResponseEntity<?> getOpenAIListOptions() {
        OpenAiChatOptions options = new OpenAiChatOptions.Builder()
                .withModel("gpt-4o")
                .withTemperature(0.4F)
                .withMaxTokens(200)
                .build();
        OpenAiChatClient client = new OpenAiChatClient(configuration.openAiKey(), options);
        ChatResponse response = client.call(new Prompt("What are the list of models I can use?"));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getResult());

    }

    @GetMapping("/slack")
    public ResponseEntity<List<Message>> getSlackChannelMessages() {
        List<Message> slackMessages = slackReader.getChannelConversations();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(slackMessages);
    }
}
