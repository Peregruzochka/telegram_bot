package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.WeekDay;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TimeHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final TimeAttribute timeAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().contains("/time-set:");
    }

    @Override
    public void compute(Update update) {
        try {
            String date = update.getCallbackQuery().getData().replace("/time-set:", "");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            WeekDay weekDay = mapper.readValue(date, WeekDay.class);
            //todo: get from backend time slots

            List<TimeSlotDto> slots = getTimeslots();
            bot.edit(timeAttribute.getText(), timeAttribute.createTimeMarkup(slots), update);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<TimeSlotDto> getTimeslots() {
        return List.of(TimeSlotDto.builder()
                .id(UUID.randomUUID())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(45))
                .build());
    }
}
