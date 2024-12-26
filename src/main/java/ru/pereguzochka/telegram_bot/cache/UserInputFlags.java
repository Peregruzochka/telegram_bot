package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
public class UserInputFlags extends Cache<Long, Map<String, Boolean>> {
    //key: chatId
    //value: map<String, Boolean>
}
