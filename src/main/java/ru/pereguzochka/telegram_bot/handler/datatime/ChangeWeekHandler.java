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
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.WeekCursorByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeWeekHandler implements UpdateHandler {

    private final WeekCursorByTelegramId weekCursorByTelegramId;
    private final DatesAttribute datesAttribute;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final TelegramBot telegramBot;
    private final BotBackendClient botBackendClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/change-week");
    }

    @Override
    public void compute(Update update) {
        String telegramId = update.getCallbackQuery().getFrom().getId().toString();

        Integer weak = weekCursorByTelegramId.get(telegramId, Integer.class).orElse(0);

        String callback = update.getCallbackQuery().getData();
        if (callback.endsWith("+")) {
            weak++;
        } else if (callback.endsWith("-")) {
            weak--;
        }
        weekCursorByTelegramId.put(telegramId, weak);

        LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        if (lesson == null || teacher == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<TimeSlotDto> timeslots = botBackendClient.getTeacherTimeSlotsInNextMonth(teacher.getId());

        String text = datesAttribute.generateText(lesson, teacher);
        InlineKeyboardMarkup markup = datesAttribute.generateDatesMarkup(timeslots, weak);

        telegramBot.edit(text, markup, update);

        log.info("telegramId: {} -> {}", telegramId, callback);
    }
}
