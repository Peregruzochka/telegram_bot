package ru.pereguzochka.telegram_bot.redis.redis_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;

import java.io.Serializable;

@Component
public class SelectedTimeSlotByTelegramId extends RedisRepository<String, TimeSlotDto> {

    protected SelectedTimeSlotByTelegramId(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String initMapName() {
        return "selectedTimeSlotByTelegramId";
    }
}
