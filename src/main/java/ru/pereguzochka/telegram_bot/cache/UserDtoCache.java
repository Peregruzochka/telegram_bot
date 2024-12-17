package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class UserDtoCache {
    //key:      telegramId
    //value:    UserDto
    private final Map<Long, UserDto> cache = new HashMap<>();

}
