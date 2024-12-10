package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.WeekCursorCache;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.datatime_handler.DateAttribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeacherChoiceHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final TeacherAttribute teacherAttribute;
    private final DateAttribute dateAttribute;
    private final RegistrationCache registrationCache;
    private final WeekCursorCache weekCursorCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery()
                && teacherAttribute.getTeacherCallbacks().contains(update.getCallbackQuery().getData());
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callback = update.getCallbackQuery().getData();

        RegistrationDto registration = registrationCache.getCache().get(telegramId);
        if (Objects.nonNull(registration)) {
            UUID teacherId = teacherAttribute.getIdByCallback(callback);
            registration.setTeacherId(teacherId);
            UUID lessonId = registration.getLessonId();

            //TODO: запрос в backend
            List<TimeSlotDto> timeSlots = getTimeSlots(lessonId, teacherId);
            int weak = 0;
            InlineKeyboardMarkup markup = dateAttribute.createWeekMarkup(timeSlots, weak);
            bot.edit(dateAttribute.getText(), markup, update);
            weekCursorCache.getCache().put(chatId, weak);
        }
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
