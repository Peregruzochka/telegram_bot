package ru.pereguzochka.telegram_bot.redis.redis_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;

import java.io.Serializable;

@Component
public class SelectTeacherByTelegramId extends RedisRepository<String, TeacherDto> {

    protected SelectTeacherByTelegramId(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String initMapName() {
        return "selectTeacherByTelegramId";
    }
}
