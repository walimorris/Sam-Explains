package com.sammy.bot.configurations;

import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.SlackClientFactory;
import com.hubspot.slack.client.SlackClientRuntimeConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class SlackConfiguration {

    @Value("${slack.bot.channelId}")
    private String slackBotChannelId;

    @Value("${slack.bot.token}")
    private String slackBotToken;

    @Bean
    public SlackClientRuntimeConfig slackClientRuntimeConfig() {
        return SlackClientRuntimeConfig.builder()
                .setTokenSupplier(() -> slackBotToken)
                .build();
    }

    @Bean
    public SlackClient slackClient() {
        return SlackClientFactory.defaultFactory().build(slackClientRuntimeConfig());
    }

    @Bean
    public String slackBotChannelId() {
        return slackBotChannelId;
    }

    @Bean
    public String slackBotToken() {
        return slackBotToken;
    }
}
