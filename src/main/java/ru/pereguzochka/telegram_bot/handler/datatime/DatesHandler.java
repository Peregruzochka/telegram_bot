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
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.WeekCursorByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatesHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final BotBackendClient botBackendClient;
    private final WeekCursorByTelegramId weekCursorByTelegramId;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final DatesAttribute datesAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/dates")
                        || hasCallback(update, "/back-to-dates");
    }

    @Override
    public void compute(Update update) {
        String telegramId = bot.extractTelegramId(update).toString();

        LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        if (lesson == null || teacher == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<TimeSlotDto> timeslots = botBackendClient.getTeacherAvailableTimeSlotsInNextMonth(teacher.getId());
        weekCursorByTelegramId.put(telegramId, 0);

        String text = datesAttribute.generateText(lesson, teacher);
        InlineKeyboardMarkup markup = datesAttribute.generateDatesMarkup(timeslots, 0);

        String callback = update.getCallbackQuery().getData();
        if (callback.equals("/dates")) {
            bot.delete(update);
            bot.send(text, markup, update);

        } else if (callback.equals("/back-to-dates")) {
            bot.edit(text, markup, update);
        }

        log.info("telegramId: {} -> {}", telegramId, callback);
    }
}