package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotsByDaysCache;
import ru.pereguzochka.telegram_bot.cache.UserRegistrationPoolCache;
import ru.pereguzochka.telegram_bot.cache.WeekCursorCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.RE_REGISTRATION;

@Component
@RequiredArgsConstructor
public class SelectReRegistrationAndDateHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final UserRegistrationPoolCache userRegistrationPoolCache;
    private final RegistrationCache registrationCache;
    private final TimeSlotCache timeSlotCache;
    private final TimeSlotsByDaysCache timeSlotsByDaysCache;
    private final WeekCursorCache weekCursorCache;
    private final DateAttribute dateAttribute;
    private final BotBackendClient backendClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/re:");
    }

    @Override
    public void compute(Update update) {
        String callback = update.getCallbackQuery().getData().replace("/re:", "");
        UUID registrationId = UUID.fromString(callback);
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto selectedRegistration = userRegistrationPoolCache.get(telegramId).get(registrationId);
        selectedRegistration.setType(RE_REGISTRATION);
        registrationCache.put(telegramId, selectedRegistration);

        UUID teacherId = selectedRegistration.getTeacher().getId();

        List<TimeSlotDto> timeSlots = backendClient.getTeacherTimeSlotsInNextMonth(teacherId);

        timeSlots.forEach(timeSlot -> timeSlotCache.put(timeSlot.getId(), timeSlot));

        Map<LocalDate, List<TimeSlotDto>> timeSlotsByDays = timeSlots.stream()
                .collect(groupingBy(slot -> slot.getStartTime().toLocalDate(), toList()));

        timeSlotsByDaysCache.put(telegramId, timeSlotsByDays);

        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        List<LocalDate> actualLocalDate = timeSlotsByDays.keySet().stream().toList();

        weekCursorCache.put(chatId, 0);

        bot.edit(dateAttribute.getText(), dateAttribute.generateLocalDateMarkup(actualLocalDate, 0), update);
    }
}
