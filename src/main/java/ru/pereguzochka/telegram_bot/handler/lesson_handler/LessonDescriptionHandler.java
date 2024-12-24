package ru.pereguzochka.telegram_bot.handler.lesson_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.LessonCache;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LessonDescriptionHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final LessonCache lessonCache;
    private final RegistrationCache registrationCache;
    private final LessonDescriptionAttribute lessonDescriptionAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/lesson-description:");
    }

    @Override
    public void compute(Update update) {
        String callback = update.getCallbackQuery().getData();
        UUID lessonId = UUID.fromString(callback.substring("/lesson-description:".length()));
        LessonDto lesson = lessonCache.get(lessonId);

        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        registrationDto.setLesson(lesson);

        bot.edit(lessonDescriptionAttribute.generateText(lesson), lessonDescriptionAttribute.createMarkup(), update);
    }
}
