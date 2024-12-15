package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class UserInputFlags {
    //key: chatId
    //value: map<String, Boolean>
    private final Map<Long, Map<String, Boolean>> flags = new HashMap<>();
}
