package ru.pereguzochka.telegram_bot.handler.lesson_handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.DeletedMessageCache;
import ru.pereguzochka.telegram_bot.cache.LessonCache;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LessonsHandler implements UpdateHandler {

    private final LessonsAttribute attribute;
    private final TelegramBot bot;
    private final BackendServiceClient backendServiceClient;
    private final BotBackendClient botBackendClient;
    private final LessonCache lessonCache;
    private final DeletedMessageCache deletedMessageCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/lessons");
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        List<Integer> deletedMessage = deletedMessageCache.get(chatId);
        if (deletedMessage != null && !deletedMessage.isEmpty()) {
            deletedMessage.forEach(messageId -> bot.delete(messageId, chatId));
            deletedMessageCache.remove(chatId);
        }

        List<LessonDto> lessons = botBackendClient.getAllLessons();
        lessons.forEach(lesson -> lessonCache.put(lesson.getId(), lesson));
        bot.edit(attribute.getText(), attribute.generateLessonsKeyboard(lessons), update);
    }
}
