package ru.pereguzochka.telegram_bot.redis.redis_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.ImageDto;

import java.io.Serializable;

@Component
public class ImageDtoByImageId extends RedisRepository<String, ImageDto> {

    protected ImageDtoByImageId(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String initMapName() {
        return "imageDtoByImageId";
    }
}
