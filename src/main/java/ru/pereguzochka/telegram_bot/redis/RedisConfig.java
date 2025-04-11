package ru.pereguzochka.telegram_bot.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.pereguzochka.telegram_bot.redis.redis_broker.FirstQuestionGroupEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.FirstQuestionEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.NotConfirmedEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.NotConfirmedGroupEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.QRSenderEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.QRSenderGroupEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.SecondQuestionEventListener;
import ru.pereguzochka.telegram_bot.redis.redis_broker.SecondQuestionGroupEventListener;

import java.io.Serializable;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final NotConfirmedEventListener notConfirmedEventListener;
    private final NotConfirmedGroupEventListener notConfirmedGroupEventListener;
    private final FirstQuestionEventListener firstQuestionEventListener;
    private final FirstQuestionGroupEventListener firstQuestionGroupEventListener;
    private final SecondQuestionEventListener secondQuestionEventListener;
    private final QRSenderEventListener qrSenderEventListener;
    private final QRSenderGroupEventListener qrSenderGroupEventListener;
    private final SecondQuestionGroupEventListener secondQuestionGroupEventListener;

    @Value("${spring.data.redis-channel.not-confirmed}")
    private String notConfirmedChannel;

    @Value("${spring.data.redis-channel.group-not-confirmed}")
    private String groupNotConfirmedChannel;

    @Value("${spring.data.redis-channel.first-question}")
    private String firstQuestionChannel;

    @Value("${spring.data.redis-channel.group-first-question}")
    private String groupFirstQuestionChannel;

    @Value("${spring.data.redis-channel.second-question}")
    private String secondQuestionChannel;

    @Value("${spring.data.redis-channel.group-second-question}")
    private String groupSecondQuestionChannel;

    @Value("${spring.data.redis-channel.qr-sender}")
    private String qrSenderChannel;

    @Value("${spring.data.redis-channel.group-qr-sender}")
    private String groupQRSenderChannel;

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .findAndRegisterModules();

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

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
                new MessageListenerAdapter(notConfirmedGroupEventListener),
                new ChannelTopic(groupNotConfirmedChannel)
        );

        container.addMessageListener(
                new MessageListenerAdapter(firstQuestionEventListener),
                new ChannelTopic(firstQuestionChannel)
        );

        container.addMessageListener(
                new MessageListenerAdapter(firstQuestionGroupEventListener),
                new ChannelTopic(groupFirstQuestionChannel)
        );

        container.addMessageListener(
                new MessageListenerAdapter(secondQuestionEventListener),
                new ChannelTopic(secondQuestionChannel)
        );

        container.addMessageListener(
                new MessageListenerAdapter(secondQuestionGroupEventListener),
                new ChannelTopic(groupSecondQuestionChannel)
        );

        container.addMessageListener(
                new MessageListenerAdapter(qrSenderEventListener),
                new ChannelTopic(qrSenderChannel)
        );

        container.addMessageListener(
                new MessageListenerAdapter(qrSenderGroupEventListener),
                new ChannelTopic(groupQRSenderChannel)
        );

        return container;
    }
}
