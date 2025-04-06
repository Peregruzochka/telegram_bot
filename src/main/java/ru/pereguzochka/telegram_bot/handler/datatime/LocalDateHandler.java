package ru.pereguzochka.telegram_bot.handler.datatime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalDateHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final TimeSlotAttribute timeSlotAttribute;
    private final BotBackendClient botBackendClient;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/local-date:");
    }

    @Override
    public void compute(Update update) {
        LocalDate localDate = LocalDate.parse(getCallbackPayload(update, "/local-date:"));
        String telegramId = update.getCallbackQuery().getFrom().getId().toString();

        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
        if (lesson == null || teacher == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<TimeSlotDto> timeslots = botBackendClient.getTeacherAvailableTimeSlotsByDate(teacher.getId(), localDate);

        String text = timeSlotAttribute.generateText(lesson, teacher, localDate);
        InlineKeyboardMarkup markup = timeSlotAttribute.createTimeMarkup(timeslots);

        bot.edit(text, markup, update);

        log.info("telegramId: {} -> {}", telegramId, update.getCallbackQuery().getData());
    }
}
