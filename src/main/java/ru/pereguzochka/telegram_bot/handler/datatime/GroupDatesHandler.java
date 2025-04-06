package ru.pereguzochka.telegram_bot.handler.datatime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.WeekCursorByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupDatesHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final BotBackendClient botBackendClient;
    private final WeekCursorByTelegramId weekCursorByTelegramId;
    private final GroupDatesAttribute groupDatesAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/group-dates")
                || hasCallback(update, "/back-to-group-dates");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        if (lesson == null || teacher == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<GroupTimeSlotDto> timeslots = botBackendClient.getTeacherAvailableGroupTimeSlotInNextMonth(teacher.getId());
        weekCursorByTelegramId.put(telegramId, 0);

        String text = groupDatesAttribute.generateText(lesson, teacher);
        InlineKeyboardMarkup markup = groupDatesAttribute.generateDatesMarkup(timeslots, 0);

        if (hasCallback(update, "/group-dates")) {
            telegramBot.delete(update);
            telegramBot.send(text, markup, update);
        } else if (hasCallback(update, "/back-to-group-dates")) {
            telegramBot.edit(text, markup, update);
        }

        log.info("telegramId: {} -> {}", telegramId, update.getCallbackQuery().getData());
    }
}
