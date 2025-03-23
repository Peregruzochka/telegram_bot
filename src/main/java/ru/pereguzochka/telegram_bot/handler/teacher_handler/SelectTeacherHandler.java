package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.ImageDtoByImageId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SelectTeacherHandler implements UpdateHandler {

    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final BotBackendClient botBackendClient;
    private final TelegramBot telegramBot;
    private final TeacherDescriptionAttribute teacherDescriptionAttribute;
    private final ImageDtoByImageId imageDtoByImageId;
    private final SelectTeacherByTelegramId selectTeacherByTelegramId;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/select-teacher:");
    }

    @Override
    public void compute(Update update) {
        UUID teacherId = UUID.fromString(update.getCallbackQuery().getData().replace("/select-teacher:", ""));
        Long telegramId = update.getCallbackQuery().getFrom().getId();

        LessonDto lesson = selectedLessonByTelegramId.get(telegramId.toString(), LessonDto.class)
                .orElse(null);

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

        selectTeacherByTelegramId.put(telegramId.toString(), teacher);

        UUID imageId = teacher.getImageID();
        ImageDto image = receiveImage(imageId);

        telegramBot.delete(update);
        telegramBot.sendImage(
                image,
                teacherDescriptionAttribute.generateText(lesson),
                teacherDescriptionAttribute.createMarkup(),
                update
        );
    }


    private ImageDto receiveImage(UUID imageId) {
        String stringImageId = imageId.toString();
        if (imageDtoByImageId.exists(stringImageId)) {
            return imageDtoByImageId.get(stringImageId, ImageDto.class).orElse(null);
        } else {
            ImageDto imageDto = botBackendClient.getImageById(imageId);
            imageDtoByImageId.put(stringImageId, imageDto);
            return imageDto;
        }
    }
}
