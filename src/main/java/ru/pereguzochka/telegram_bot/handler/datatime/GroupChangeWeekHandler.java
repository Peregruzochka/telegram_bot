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

@Component
@Slf4j
@RequiredArgsConstructor
public class GroupChangeWeekHandler implements UpdateHandler {

    private final WeekCursorByTelegramId weekCursorByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final TelegramBot telegramBot;
    private final BotBackendClient botBackendClient;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final GroupDatesAttribute groupDatesAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/group-change-week");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();

        Integer weak = weekCursorByTelegramId.get(telegramId, Integer.class).orElse(0);

        String callback = update.getCallbackQuery().getData();
        if (callback.endsWith("+")) {
            weak++;
        } else if (callback.endsWith("-")) {
            weak--;
        }
        weekCursorByTelegramId.put(telegramId, weak);

        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
        if (lesson == null || teacher == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<GroupTimeSlotDto> timeslots = botBackendClient.getTeacherAvailableGroupTimeSlotInNextMonth(teacher.getId());
        String text = groupDatesAttribute.generateText(lesson, teacher);
        InlineKeyboardMarkup markup = groupDatesAttribute.generateDatesMarkup(timeslots, weak);
        telegramBot.edit(text, markup, update);

        log.info("telegramId: {} -> {}", telegramId, callback);
    }
}
