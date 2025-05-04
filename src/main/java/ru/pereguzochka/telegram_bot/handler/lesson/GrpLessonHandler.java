package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpLessonHandler implements UpdateHandler {
    private final BotBackendClient backendClient;
    private final GrpLessonAttribute grpLessonAttribute;
    private final TelegramBot telegramBot;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/grp-lesson");
    }

    @Override
    public void compute(Update update) {
        List<GroupLessonDto> lessons = backendClient.getAllGroupLessons();

        String text = grpLessonAttribute.getText();
        InlineKeyboardMarkup markup = grpLessonAttribute.generateGroupLessonMarkup(lessons);

        telegramBot.edit(text, markup, update);

        Long telegramId = telegramBot.extractTelegramId(update);
        log.info("telegramId: {} -> /grp-lesson", telegramId);
    }
}
