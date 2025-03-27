package ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.pereguzochka.telegram_bot.redis.redis_repository.FlagByTelegramId;

import java.io.Serializable;

@Repository
public class InputAddChildBirthdayByTelegramId extends FlagByTelegramId {
    protected InputAddChildBirthdayByTelegramId(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String initMapName() {
        return "inputAddChildBirthdayByTelegramId";
    }
}
