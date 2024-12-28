package ru.pereguzochka.telegram_bot.cache;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;

import java.util.Map;
import java.util.UUID;

@Component
public class UserRegistrationPoolCache extends Cache<Long, Map<UUID, RegistrationDto>>{
    //key -> telegramId
}
