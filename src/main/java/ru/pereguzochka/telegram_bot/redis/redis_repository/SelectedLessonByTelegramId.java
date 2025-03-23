package ru.pereguzochka.telegram_bot.redis.redis_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.pereguzochka.telegram_bot.dto.LessonDto;

import java.io.Serializable;

@Repository
public class SelectedLessonByTelegramId extends RedisRepository<String, LessonDto> {

    protected SelectedLessonByTelegramId(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String initMapName() {
        return "selectedLessonByTelegramId";
    }
}
