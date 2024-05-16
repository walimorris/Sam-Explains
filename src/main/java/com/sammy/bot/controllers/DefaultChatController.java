package com.sammy.bot.controllers;

import com.sammy.bot.services.impl.SlackPosterServiceImpl;
import com.sammy.bot.services.impl.SlackReaderServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sammy/api")
public class DefaultChatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultChatController.class);

    private final SlackReaderServiceImpl slackReaderService;
    private final SlackPosterServiceImpl slackPosterService;

    private static final String TEXT = "text";

    public DefaultChatController(SlackReaderServiceImpl slackReaderService, SlackPosterServiceImpl slackPosterService) {
        this.slackReaderService = slackReaderService;
        this.slackPosterService = slackPosterService;
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> getSlackChannelMessages() {
        List<Map<String, String>> slackContext = slackReaderService.getChannelConversations();
        String channelSummary = slackReaderService.promptForSlackChannelInteractionsSummary(slackContext);
        slackPosterService.postMessage(channelSummary);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(channelSummary);
    }

    @PostMapping("/ask")
    public ResponseEntity<String> getQuestionAnswer(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(Collectors.joining());
        LOGGER.info(body);
        Map<String, String> payloadMap = slackReaderService.mapPayload(body);
        List<Map<String, String>> slackContext = slackReaderService.getChannelConversations();
        String answer = slackReaderService.promptSlackForSlackUserQuestion(payloadMap.get(TEXT), slackContext);
        slackPosterService.postMessage(answer);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(answer);
    }
}
