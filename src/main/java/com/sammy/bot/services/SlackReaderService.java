package com.sammy.bot.services;

import java.io.IOException;
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

    /**
     * Uses a list of Slack chat interactions/messages and a user question to prompt an answer
     * of the question.
     *
     * @param question {@link String} question
     * @param slackChatContext {@link List} of slack interactions to better answer the prompt
     *
     * @return {@link String} answer to the question
     */
    String promptSlackForSlackUserQuestion(String question, List<Map<String, String>> slackChatContext);

    /**
     * Slack payloads that contain parameters, i.e. text after running a slash command can be found
     * within the overall post request payload. This payload contains important information about
     * the Slack channel, users, text context, and so on. This method parses that information and
     * builds it into a {@link Map} structure.
     *
     * @param payload {@link String} payload body
     * @return {@link Map}
     *
     * @see <a href=https://api.slack.com/interactivity/slash-commands>Slack slash commands</a>
     * @throws IOException IO exception during payload deconstruction and reconstruction
     */
    Map<String, String> mapPayload(String payload) throws IOException;
}
