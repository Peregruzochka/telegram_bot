package ru.pereguzochka.telegram_bot.handler.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SelectTeacherHandler implements UpdateHandler {
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final TeacherDescriptionAttribute teacherDescriptionAttribute;
    private final TelegramBot telegramBot;
    private final BotBackendClient botBackendClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/select-teacher:");
    }

    @Override
    public void compute(Update update) {
        UUID teacherId = UUID.fromString(update.getCallbackQuery().getData().replace("/select-teacher:", ""));
        String telegramId = update.getCallbackQuery().getFrom().getId().toString();
        LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
        if (lesson == null) {
            restartBotMessageSender.send(update);
            return;
        }

        TeacherDto teacher = lesson.getTeachers().stream()
                .filter(t -> t.getId().equals(teacherId))
                .findFirst()
                .orElse(null);

        if (teacher == null) {
            restartBotMessageSender.send(update);
            return;
        }

        selectedTeacherByTelegramId.put(telegramId, teacher);

        String text = teacherDescriptionAttribute.generateText(lesson, teacher);
        InlineKeyboardMarkup markup = teacherDescriptionAttribute.createMarkup();

        UUID imageId = teacher.getImageID();
        ImageDto image = botBackendClient.getImageById(imageId);

        telegramBot.delete(update);
        telegramBot.sendImage(image, text, markup, update);
    }
}
