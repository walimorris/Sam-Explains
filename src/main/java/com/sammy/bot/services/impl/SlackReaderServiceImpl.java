package com.sammy.bot.services.impl;

import com.sammy.bot.configurations.ApplicationPropertiesConfiguration;
import com.sammy.bot.configurations.SlackConfiguration;
import com.sammy.bot.services.SlackReaderService;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.Message;
import com.slack.api.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SlackReaderServiceImpl implements SlackReaderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackReaderServiceImpl.class);
    private final ApplicationPropertiesConfiguration configuration;
    private final SlackConfiguration slackConfiguration;

    private static final String USER = "user";
    private static final String TIME_STAMP = "timestamp";
    private static final String MESSAGE = "message";

    public SlackReaderServiceImpl(ApplicationPropertiesConfiguration configuration, SlackConfiguration slackConfiguration) {
        this.configuration = configuration;
        this.slackConfiguration = slackConfiguration;
    }

    @Override
    public List<Map<String, String>> getChannelConversations() {
        Slack slack = Slack.getInstance();
        MethodsClient slackMethodsClient = slack.methods(slackConfiguration.slackBotToken());

        // history dates
        LocalDateTime startTimeUTC = LocalDateTime.of(2024, Month.MAY, 12, 10, 0);
        LocalDateTime endTimeUTC = LocalDateTime.of(2024, Month.MAY, 13, 23, 0);
        long startTime = startTimeUTC.atZone(ZoneOffset.UTC).toEpochSecond();
        long endTime = endTimeUTC.atZone(ZoneOffset.UTC).toEpochSecond();

        ConversationsHistoryRequest conversationsHistoryRequest = ConversationsHistoryRequest.builder()
                .channel(slackConfiguration.slackBotChannelId())
                .limit(10)
                .build();

        List<Map<String, String>> messages = new ArrayList<>();
        try {
            ConversationsHistoryResponse response = slackMethodsClient.conversationsHistory(conversationsHistoryRequest);
            if (response != null && response.isOk()) {
                LOGGER.info(String.valueOf(response.getMessages().size()));
                for (Message message : response.getMessages()) {
                    String userId = message.getUser();
                    String timeStamp = formatTimestamp(message.getTs());
                    UsersInfoRequest usersInfoRequest = UsersInfoRequest.builder()
                            .user(userId)
                            .build();
                    UsersInfoResponse usersInfoResponse = slackMethodsClient.usersInfo(usersInfoRequest);
                    if (usersInfoResponse != null && usersInfoResponse.isOk()) {
                        User user = usersInfoResponse.getUser();
                        Map<String, String> userMessage = Map.of(
                                USER, user.getName(),
                                TIME_STAMP, timeStamp,
                                MESSAGE, message.getText()
                        );
                        messages.add(userMessage);
                    }
                }
                return messages;
            }
        } catch (IOException | SlackApiException e) {
            LOGGER.error("Error fetching slack messages from channel '{}': {}", slackConfiguration.slackBotChannelId(), e.getMessage());
        }
        return null;
    }

    @Override
    public String promptForSlackChannelInteractionsSummary(List<Map<String, String>> slackMessages) {
        OpenAiChatOptions options = new OpenAiChatOptions.Builder()
                .withModel("gpt-4o")
                .withTemperature(0.4F)
                .withMaxTokens(200)
                .build();
        OpenAiChatClient client = new OpenAiChatClient(configuration.openAiKey(), options);
        ChatResponse response = client.call(new Prompt("In sentence form, give a summary of this " +
                "list of slack interactions" + slackMessages));
        return response.getResult().getOutput().getContent();
    }

    private static String formatTimestamp(String ts) {
        double timestamp = Double.parseDouble(ts);
        Instant instant = Instant.ofEpochSecond((long) timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return dateTime.toString();
    }
}
