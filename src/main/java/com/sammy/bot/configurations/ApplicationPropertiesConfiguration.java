package com.sammy.bot.configurations;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationPropertiesConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongodbUri;

    @Value("${spring.ai.openai.api-key}")
    private String openAiKey;

    @Bean
    public String mongodbUri() {
        return this.mongodbUri;
    }

    @Bean
    public OpenAiApi openAiKey() {
        return new OpenAiApi(openAiKey);
    }
}
