package ru.pereguzochka.telegram_bot.cache;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class TimeSlotsByDaysCache extends Cache<Long, Map<LocalDate, List<TimeSlotDto>>> {
    //key -> long telegramId
    //localDate -> day
    //List<TimeSlotDto> -> список timeslot в этот день
}
