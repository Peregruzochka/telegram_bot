package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
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
    private final RegistrationCache registrationCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/change-week");
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Integer weak = cache.get(chatId);
        if (weak == null) {
            weak = 0;
        }

        String callback = update.getCallbackQuery().getData();
        if (callback.endsWith("+")) {
            weak++;
        } else if (callback.endsWith("-")) {
            weak--;
        }
        cache.put(chatId, weak);

        Long telegramId = update.getCallbackQuery().getFrom().getId();
        List<LocalDate> actualLocalDate = timeSlotsByDaysCache.get(telegramId).keySet().stream().toList();

        boolean hiddenStatus = registrationCache.get(telegramId).getTeacher().isHidden();

        InlineKeyboardMarkup markup;
        if (hiddenStatus) {
            markup = attribute.generateHideTeacherLocalDateMarkup(actualLocalDate, weak);
        } else {
            markup = attribute.generateLocalDateMarkup(actualLocalDate, weak);
        }

        bot.edit(attribute.getText(), markup, update);
    }
}
