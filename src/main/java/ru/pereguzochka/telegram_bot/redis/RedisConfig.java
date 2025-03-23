package ru.pereguzochka.telegram_bot.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.pereguzochka.telegram_bot.redis.redis_broker.FirstQuestionEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.NotConfirmedEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.QRSenderEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.SecondQuestionEventListener;

import java.io.Serializable;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final NotConfirmedEventListener notConfirmedEventListener;
    private final FirstQuestionEventListener firstQuestionEventListener;
    private final SecondQuestionEventListener secondQuestionEventListener;
    private final QRSenderEventListener qrSenderEventListener;

    @Value("${spring.data.redis-channel.not-confirmed}")
    private String notConfirmedChannel;

    @Value("${spring.data.redis-channel.first-question}")
    private String firstQuestionChannel;

    @Value("${spring.data.redis-channel.second-question}")
    private String secondQuestionChannel;

    @Value("${spring.data.redis-channel.qr-sender}")
    private String qrSenderChannel;

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<Serializable> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Serializable.class);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }

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

        container.addMessageListener(
                new MessageListenerAdapter(qrSenderEventListener),
                new ChannelTopic(qrSenderChannel)
        );

        return container;
    }
}
