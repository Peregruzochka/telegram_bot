package ru.pereguzochka.telegram_bot.cache;

import org.springframework.stereotype.Component;

@Component
public class WeekCursorCache extends Cache<Long, Integer> {
//    Key:      ChatId
//    Value:    nextWeek
}
