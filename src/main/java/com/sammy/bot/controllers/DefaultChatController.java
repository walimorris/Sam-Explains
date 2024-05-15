package com.sammy.bot.controllers;

import com.sammy.bot.services.impl.SlackPosterServiceImpl;
import com.sammy.bot.services.impl.SlackReaderServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sammy/api/default")
public class DefaultChatController {
    private final SlackReaderServiceImpl slackReaderService;
    private final SlackPosterServiceImpl slackPosterService;

    public DefaultChatController(SlackReaderServiceImpl slackReaderService, SlackPosterServiceImpl slackPosterService) {
        this.slackReaderService = slackReaderService;
        this.slackPosterService = slackPosterService;
    }

    @GetMapping("/slack")
    public ResponseEntity<String> getSlackChannelMessages() {
        List<Map<String, String>> slackMessages = slackReaderService.getChannelConversations();
        String channelSummary = slackReaderService.promptForSlackChannelInteractionsSummary(slackMessages);
        slackPosterService.postMessage(channelSummary);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(channelSummary);
    }
}
