package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.UserDto;

@Component
@Getter
public class UserDtoCache extends Cache<Long, UserDto> {
    //key -> tgId
}
