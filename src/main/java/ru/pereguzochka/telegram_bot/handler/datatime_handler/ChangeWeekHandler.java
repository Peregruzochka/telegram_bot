package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.WeekCursorCache;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ChangeWeekHandler implements UpdateHandler {

    private final TelegramBot bot;
    private final DateAttribute attribute;
    private final WeekCursorCache cache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/changeweek");
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer weak = cache.getCache().get(chatId);
        String callback = update.getCallbackQuery().getData();
        if (callback.endsWith("+")) {
            weak++;
        } else if (callback.endsWith("-")) {
            weak--;
        }
        bot.edit(attribute.getText(), attribute.createWeekMarkup(getTimeSlots(null, null), weak), update);
        cache.getCache().put(chatId, weak);
    }

    private List<TimeSlotDto> getTimeSlots(UUID lessonId, UUID teacherId) {
        return IntStream.range(0, 3)
                .mapToObj(i -> TimeSlotDto.builder()
                        .id(UUID.randomUUID())
                        .startTime(LocalDateTime.now().plusDays(i))
                        .endTime(LocalDateTime.now().plusDays(i).plusMinutes(45))
                        .build()
                )
                .toList();
    }
}
