package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotsByDaysCache;
import ru.pereguzochka.telegram_bot.cache.WeekCursorCache;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class DateHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final WeekCursorCache weekCursorCache;
    private final BackendServiceClient backendServiceClient;
    private final RegistrationCache registrationCache;
    private final TimeSlotsByDaysCache timeSlotsByDaysCache;
    private final DateAttribute dataTimeAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/date");
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        int weak = 0;
        weekCursorCache.put(chatId, weak);

        UUID teacherId = registrationCache.get(telegramId).getTeacher().getId();

        List<TimeSlotDto> timeSlots = backendServiceClient.getMonthFreeTeacherTimeSlots(teacherId);
        Map<LocalDate, List<TimeSlotDto>> timeSlotsByDays = timeSlots.stream()
                .collect(groupingBy(slot -> slot.getStartTime().toLocalDate(), toList()));

        timeSlotsByDaysCache.put(telegramId, timeSlotsByDays);

        List<LocalDate> actualLocalDate = timeSlotsByDays.keySet().stream().toList();
        bot.edit(dataTimeAttribute.getText(), dataTimeAttribute.generateLocalDateMarkup(actualLocalDate, weak), update);
    }
}