package ru.pereguzochka.telegram_bot.redis.redis_repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.pereguzochka.telegram_bot.dto.UserDto;

import java.io.Serializable;

@Repository
public class UsersByTelegramId extends RedisRepository<String, UserDto> {

    protected UsersByTelegramId(RedisTemplate<String, Serializable> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String initMapName() {
        return "usersByTelegramId";
    }
}

