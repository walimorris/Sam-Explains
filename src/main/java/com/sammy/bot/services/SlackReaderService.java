package com.sammy.bot.services;

import java.util.List;
import java.util.Map;

public interface SlackReaderService {

    /**
     * Gets a list of Slack messages containing information about the message text, user
     * and timestamp.
     *
     * @return {@link List}
     */
    List<Map<String, String>> getChannelConversations();

    /**
     * Uses a List of slack interactions/messages to prompt openai to generate a summary.
     *
     * @param slackMessages {@link List} messages/interactions on slack channel
     *
     * @return {@link String} summary of Slack channel messages/interactions
     */
    String promptForSlackChannelInteractionsSummary(List<Map<String, String>> slackMessages);
}
