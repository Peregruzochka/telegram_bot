package ru.pereguzochka.telegram_bot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "bot-backend", url = "${bot-backend.host}:${bot-backend.port}")
public interface BotBackendClient {
    @GetMapping("/users")
    UserDto getUserByTelegramId(@RequestParam(name = "telegram-id") Long telegramId);

    @GetMapping("/lessons/all")
    List<LessonDto> getAllLessons();

    @GetMapping("/images/{image-id}")
    ImageDto getImageById(@PathVariable(name = "image-id") UUID imageId);
}
