package ru.pereguzochka.telegram_bot.handler.teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupTeacherHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final BotBackendClient botBackendClient;
    private final GroupTeachersAttribute groupTeachersAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/group-teachers")
                || hasCallback(update, "/back-to-group-teachers");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
        if (lesson == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<TeacherDto> teachers = botBackendClient.getTeachersByGroupLesson(lesson.getId());

        String text =  groupTeachersAttribute.generateText(lesson);
        InlineKeyboardMarkup markup = groupTeachersAttribute.generateGroupTeacherMarkup(teachers);

        if (hasCallback(update, "/group-teachers")) {
            telegramBot.edit(text, markup, update);
        }
        else if (hasCallback(update,"/back-to-group-teachers")) {
            telegramBot.delete(update);
            telegramBot.send(text, markup, update);
        }

        log.info("telegramId: {} -> {}", telegramId, update.getCallbackQuery().getData());
    }
}
