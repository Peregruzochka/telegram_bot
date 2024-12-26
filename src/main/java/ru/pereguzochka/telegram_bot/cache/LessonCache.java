package ru.pereguzochka.telegram_bot.cache;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.LessonDto;

import java.util.UUID;

@Component
public class LessonCache extends Cache<UUID, LessonDto> {

}
