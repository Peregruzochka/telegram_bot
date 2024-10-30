package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class RegistrationCache {
    private final Map<Long, RegistrationDto> cache = new HashMap<>();
}
