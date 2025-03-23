package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeachersHandler implements UpdateHandler {
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final TeachersAttribute teachersAttribute;
    private final TelegramBot telegramBot;


    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && (
                update.getCallbackQuery().getData().equals("/teachers")
                || update.getCallbackQuery().getData().equals("/back-to-teachers"));
    }

    @Override
    public void compute(Update update) {
        String telegramId = update.getCallbackQuery().getFrom().getId().toString();
        LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
        if (lesson == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<TeacherDto> teachers = lesson.getTeachers();

        String text =  teachersAttribute.generateText(lesson);
        InlineKeyboardMarkup markup = teachersAttribute.generateTeacherMarkup(teachers);

        String callback = update.getCallbackQuery().getData();
        if (callback.equals("/teachers")) {
            telegramBot.edit(text, markup, update);
        }
        else if (callback.equals("/back-to-teachers")) {
            telegramBot.delete(update);
            telegramBot.send(text, markup, update);
        }
    }
}
