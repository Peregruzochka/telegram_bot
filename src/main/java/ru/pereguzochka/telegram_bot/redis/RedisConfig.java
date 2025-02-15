package ru.pereguzochka.telegram_bot.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final NotConfirmedEventListener notConfirmedEventListener;
    private final FirstQuestionEventListener firstQuestionEventListener;
    private final SecondQuestionEventListener secondQuestionEventListener;

    @Value("${spring.data.redis-channel.not-confirmed}")
    private String notConfirmedChannel;

    @Value("${spring.data.redis-channel.first-question}")
    private String firstQuestionChannel;

    @Value("${spring.data.redis-channel.second-question}")
    private String secondQuestionChannel;


    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(
                new MessageListenerAdapter(notConfirmedEventListener),
                new ChannelTopic(notConfirmedChannel)
        );

        container.addMessageListener(
                new MessageListenerAdapter(firstQuestionEventListener),
                new ChannelTopic(firstQuestionChannel)
        );

        container.addMessageListener(
                new MessageListenerAdapter(secondQuestionEventListener),
                new ChannelTopic(secondQuestionChannel)
        );

        return container;
    }
}
