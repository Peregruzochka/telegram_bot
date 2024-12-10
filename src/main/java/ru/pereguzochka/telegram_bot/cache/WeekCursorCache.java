package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class WeekCursorCache {
//    Key:      ChatId
//    Value:    nextWeek
    private final Map<Long, Integer> cache = new HashMap<>();
}
