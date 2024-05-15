package com.sammy.bot.services.impl;

import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.sammy.bot.configurations.SlackConfiguration;
import com.sammy.bot.services.SlackPosterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SlackPosterServiceImpl implements SlackPosterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackReaderServiceImpl.class);

    private final SlackConfiguration slackConfiguration;
    private final SlackClient slackClient;

    public SlackPosterServiceImpl(SlackConfiguration slackConfiguration, SlackClient slackHttpClient) {
        this.slackConfiguration = slackConfiguration;
        this.slackClient = slackHttpClient;
    }

    @Override
    public void postMessage(String message) {
        slackClient.postMessage(ChatPostMessageParams.builder()
                .setText(message)
                .setChannelId(slackConfiguration.slackBotChannelId())
                .build());
    }
}
