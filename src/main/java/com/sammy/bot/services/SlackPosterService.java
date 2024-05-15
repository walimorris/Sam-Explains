package com.sammy.bot.services;

public interface SlackPosterService {

    /**
     * Posts a Slack Message.
     *
     * @param message {@link String}
     */
    void postMessage(String message);
}
