package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.DeletedMessageCache;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotsByDaysCache;
import ru.pereguzochka.telegram_bot.cache.WeekCursorCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Component
@RequiredArgsConstructor
public class ChooseTeacherAndDateHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final RegistrationCache registrationCache;
    private final DateAttribute dateAttribute;
    private final TimeSlotsByDaysCache timeSlotsByDaysCache;
    private final TimeSlotCache timeSlotCache;
    private final DeletedMessageCache deletedMessageCache;
    private final WeekCursorCache weekCursorCache;
    private final BotBackendClient backendClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/teacher:");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        String callback = update.getCallbackQuery().getData();
        UUID teacherId = UUID.fromString(callback.replace("/teacher:", ""));
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        LessonDto lessonDto = registrationDto.getLesson();
        TeacherDto teacherDto = lessonDto.getTeachers().stream()
                .filter(teacher -> teacher.getId().equals(teacherId))
                .findFirst().orElseThrow();
        registrationDto.setTeacher(teacherDto);

        List<TimeSlotDto> timeSlots = backendClient.getTeacherTimeSlotsInNextMonth(teacherId);
        timeSlots.removeIf(slot -> slot.getStartTime().isBefore(LocalDateTime.now().plusHours(3)));

        timeSlots.forEach(timeSlot -> timeSlotCache.put(timeSlot.getId(), timeSlot));

        Map<LocalDate, List<TimeSlotDto>> timeSlotsByDays = timeSlots.stream()
                .collect(groupingBy(slot -> slot.getStartTime().toLocalDate(), toList()));

        timeSlotsByDaysCache.put(telegramId, timeSlotsByDays);

        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        List<Integer> deletedMessage = deletedMessageCache.get(chatId);
        deletedMessage.forEach(messageId -> bot.delete(messageId, chatId));
        deletedMessageCache.remove(chatId);

        List<LocalDate> actualLocalDate = timeSlotsByDays.keySet().stream().toList();

        weekCursorCache.put(chatId, 0);

        bot.edit(dateAttribute.getText(), dateAttribute.generateLocalDateMarkup(actualLocalDate, 0), update);
    }
}
