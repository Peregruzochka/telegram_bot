package ru.pereguzochka.telegram_bot.client;

import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;

import java.util.List;
import java.util.UUID;


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

    public ImageDto getImageById(UUID imageId) {
        //TODO: настроить взаимодействие
        return null;
    }

    public List<TimeSlotDto> getMonthFreeTeacherTimeSlots(UUID teacherId) {
        //TODO: настроить взаимодействие
        return null;
    }
}
