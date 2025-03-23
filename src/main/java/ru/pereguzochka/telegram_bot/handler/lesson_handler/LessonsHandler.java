package ru.pereguzochka.telegram_bot.handler.lesson_handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

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
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/lessons");
    }

    @Override
    public void compute(Update update) {

        List<LessonDto> lessons = botBackendClient.getAllLessons();
        //getGroup!!!
        telegramBot.edit(lessonsAttribute.getText(), lessonsAttribute.generateLessonsKeyboard(lessons), update);

        Long telegramId = update.getCallbackQuery().getFrom().getId();
        log.info("telegramId: {} -> /lessons", telegramId);
    }
}
