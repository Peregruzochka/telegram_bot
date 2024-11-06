package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.teacher_handler.TeacherAttribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DateTimeHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final TeacherAttribute teacherAttribute;
    private final RegistrationCache registrationCache;
    private final DateTimeAttribute dataTimeAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/datetime");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        String callback = update.getCallbackQuery().getData();
    }

    private List<TimeSlotDto> getSlots(UUID lessonId, UUID teacherId) {
        return IntStream.range(1, 10)
                .mapToObj(i -> TimeSlotDto.builder()
                        .id(UUID.randomUUID())
                        .startTime(LocalDateTime.now().minusDays(i))
                        .endTime(LocalDateTime.now().minusDays(i).plusMinutes(45))
                        .build()
                )
                .toList();
    }
}