package ru.pereguzochka.telegram_bot.cache;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;

import java.util.UUID;

@Component
public class TimeSlotCache extends Cache<UUID, TimeSlotDto>{
    //key -> timeslotId
}
