package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.TimeSlotsByDaysCache;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TimeHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final TimeAttribute timeAttribute;
    private final TimeSlotsByDaysCache timeSlotsByDaysCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().contains("/local-date:");
    }

    @Override
    public void compute(Update update) {
        String callback = update.getCallbackQuery().getData();
        LocalDate localDate = LocalDate.parse(callback.replace("/local-date:", ""));
        Long telegramId = update.getCallbackQuery().getFrom().getId();

        List<TimeSlotDto> actualSlots = timeSlotsByDaysCache.get(telegramId).get(localDate);


    }

}
