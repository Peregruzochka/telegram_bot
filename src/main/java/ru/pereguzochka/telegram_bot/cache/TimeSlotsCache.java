package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Getter
public class TimeSlotsCache {
    // key -> slotId
    private final Map<UUID, TimeSlotDto> cache = new HashMap<>();
}
