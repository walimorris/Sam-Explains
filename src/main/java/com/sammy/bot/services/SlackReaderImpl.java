package com.sammy.bot.services;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class SlackReaderImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackReaderImpl.class);
    private static final String SLACK_BOT_TOKEN = "<SLACK_TOKEN>";
    private static final String SLACK_CHANNEL_ID = "<CHANNEL_ID>";

    public List<Message> getChannelConversations() {
        Slack slack = Slack.getInstance();
        MethodsClient slackMethodsClient = slack.methods(SLACK_BOT_TOKEN);

        // history dates
        LocalDateTime startTimeUTC = LocalDateTime.of(2024, Month.MAY, 12, 10, 0);
        LocalDateTime endTimeUTC = LocalDateTime.of(2024, Month.MAY, 13, 23, 0);
        long startTime = startTimeUTC.atZone(ZoneOffset.UTC).toEpochSecond();
        long endTime = endTimeUTC.atZone(ZoneOffset.UTC).toEpochSecond();

        ConversationsHistoryRequest conversationsHistoryRequest = ConversationsHistoryRequest.builder()
                .channel(SLACK_CHANNEL_ID)
                .limit(10)
                .build();

        try {
            ConversationsHistoryResponse response = slackMethodsClient.conversationsHistory(conversationsHistoryRequest);
            if (response != null && response.isOk()) {
                LOGGER.info(String.valueOf(response.getMessages().size()));
                return response.getMessages();
            }
        } catch (IOException | SlackApiException e) {
            LOGGER.error("Error fetching slack messages from channel '{}': {}", SLACK_CHANNEL_ID, e.getMessage());
        }
        LOGGER.info("Empty list");
        Message emptyMessage = new Message();
        emptyMessage.setText("empty");
        return List.of(emptyMessage);
    }
}
