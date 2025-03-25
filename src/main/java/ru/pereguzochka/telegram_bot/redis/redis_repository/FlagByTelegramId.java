package ru.pereguzochka.telegram_bot.redis.redis_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

public abstract class FlagByTelegramId extends RedisRepository<String, Boolean> {

    protected FlagByTelegramId(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public boolean isTrue(String telegramId) {
        return super.get(telegramId, Boolean.class).orElse(false);
    }

    public void setTrue(String telegramId) {
        super.put(telegramId, Boolean.TRUE);
    }

    public void setFalse(String telegramId) {
        super.put(telegramId, Boolean.FALSE);
    }
}
