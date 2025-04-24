package ru.pereguzochka.telegram_bot.handler.teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeachersHandler implements UpdateHandler {
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final TeachersAttribute teachersAttribute;
    private final TelegramBot telegramBot;
    private final BotBackendClient botBackendClient;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/teachers")
                || hasCallback(update, "/back-to-teachers");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
        if (lesson == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<TeacherDto> teachers = botBackendClient.getTeachersByLesson(lesson.getId());

        String text =  teachersAttribute.generateText(lesson);
        InlineKeyboardMarkup markup = teachersAttribute.generateTeacherMarkup(teachers);

        if (hasCallback(update, "/teachers")) {
            telegramBot.edit(text, markup, update);
        }
        else if (hasCallback(update,"/back-to-teachers")) {
            telegramBot.delete(update);
            telegramBot.send(text, markup, update);
        }

        log.info("telegramId: {} -> {}", telegramId, update.getCallbackQuery().getData());
    }
}
