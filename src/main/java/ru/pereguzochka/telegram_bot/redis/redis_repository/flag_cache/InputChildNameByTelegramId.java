package ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.redis.redis_repository.FlagByTelegramId;

import java.io.Serializable;

@Component
public class InputChildNameByTelegramId extends FlagByTelegramId {
    protected InputChildNameByTelegramId(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String initMapName() {
        return "inputChildNameByTelegramId";
    }
}
