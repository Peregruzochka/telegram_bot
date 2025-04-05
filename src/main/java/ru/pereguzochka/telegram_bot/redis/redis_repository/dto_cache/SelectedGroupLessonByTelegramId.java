package ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.redis.redis_repository.RedisRepository;

import java.io.Serializable;

@Repository
public class SelectedGroupLessonByTelegramId extends RedisRepository<String, GroupLessonDto> {
    protected SelectedGroupLessonByTelegramId(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String initMapName() {
        return "selectedGroupLessonByTelegramId";
    }
}
