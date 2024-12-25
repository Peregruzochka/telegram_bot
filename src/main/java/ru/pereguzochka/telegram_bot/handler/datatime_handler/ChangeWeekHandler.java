package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.TimeSlotsByDaysCache;
import ru.pereguzochka.telegram_bot.cache.WeekCursorCache;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChangeWeekHandler implements UpdateHandler {

    private final TelegramBot bot;
    private final DateAttribute attribute;
    private final WeekCursorCache cache;
    private final TimeSlotsByDaysCache timeSlotsByDaysCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/change-week");
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Integer weak = cache.get(chatId);
        String callback = update.getCallbackQuery().getData();
        if (callback.endsWith("+")) {
            weak++;
        } else if (callback.endsWith("-")) {
            weak--;
        }
        cache.put(chatId, weak);

        Long telegramId = update.getCallbackQuery().getFrom().getId();
        List<LocalDate> actualLocalDate = timeSlotsByDaysCache.get(telegramId).keySet().stream().toList();

        bot.edit(attribute.getText(), attribute.generateLocalDateMarkup(actualLocalDate, weak), update);

    }
}
