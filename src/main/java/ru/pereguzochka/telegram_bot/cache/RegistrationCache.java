package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;

@Component
@Getter
public class RegistrationCache extends Cache<Long, RegistrationDto> {
    //key -> telegramId
}
