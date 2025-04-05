package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import javax.swing.*;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LessonsHandler implements UpdateHandler {

    private final BotBackendClient botBackendClient;
    private final TelegramBot telegramBot;
    private final LessonsAttribute lessonsAttribute;


    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/lessons");
    }

    @Override
    public void compute(Update update) {

        List<LessonDto> lessons = botBackendClient.getAllLessons();
        List<GroupLessonDto> groupLessons = botBackendClient.getAllGroupLessons();

        String text = lessonsAttribute.getText();
        InlineKeyboardMarkup markup = lessonsAttribute.generateLessonsKeyboard(lessons, groupLessons);

        telegramBot.edit(text, markup, update);

        Long telegramId = update.getCallbackQuery().getFrom().getId();
        log.info("telegramId: {} -> /lessons", telegramId);
    }
}
