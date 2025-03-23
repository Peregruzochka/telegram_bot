package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageAttribute;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeachersHandler implements UpdateHandler {
    private final TelegramBot telegramBot;

    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final RestartBotMessageAttribute restartBotMessageAttribute;
    private final TeachersAttribute teachersAttribute;
    private final RestartBotMessageSender restartBotMessageSender;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && (
                update.getCallbackQuery().getData().equals("/teachers")
                        || update.getCallbackQuery().getData().equals("/back-to-teachers"));
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        LessonDto lesson = selectedLessonByTelegramId.get(telegramId.toString(), LessonDto.class)
                .orElse(null);

        if (lesson == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<TeacherDto> notHiddenTeacher = lesson.getTeachers().stream()
                .dropWhile(TeacherDto::isHidden)
                .toList();

        if (notHiddenTeacher.isEmpty()) {
            telegramBot.send(restartBotMessageAttribute.getText(), update);
        } else {
            String text = teachersAttribute.generateText(lesson);
            InlineKeyboardMarkup markup = teachersAttribute.generateTeacherMarkup(notHiddenTeacher);

            String callback = update.getCallbackQuery().getData();

            if (callback.equals("/teachers")) {
                telegramBot.edit(text, markup, update);
            } else if (callback.equals("/back-to-teachers")) {
                telegramBot.delete(update);
                telegramBot.send(text, markup, update);
            }
        }

        log.info("telegram id: {} -> /teachers", telegramId);
    }
}
