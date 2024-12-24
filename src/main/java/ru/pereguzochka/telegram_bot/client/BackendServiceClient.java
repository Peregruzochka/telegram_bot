package ru.pereguzochka.telegram_bot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;

import java.util.List;


@Component
public class BackendServiceClient {

    public UserDto getUserByTelegramId(Long telegramId) {
        //TODO: настроить взаимодействие с backend
        return null;
    }

    public List<LessonDto> getAllLessons() {
        //TODO: настроить взаимодействие
        return null;
    }
}
