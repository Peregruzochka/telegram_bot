package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.DeletedMessageCache;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.ImageCache;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeacherHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final TeacherAttribute attribute;
    private final RegistrationCache registrationCache;
    private final ImageCache imageCache;
    private final DeletedMessageCache deletedMessageCache;
    private final BackendServiceClient backendServiceClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/teachers");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        List<TeacherDto> teachers = registrationDto.getLesson().getTeachers();
        List<ImageDto> images = teachers.stream()
                .map(TeacherDto::getImageID)
                .map(this::getTeacherImage)
                .toList();

        bot.delete(update);
        List<Integer> messageId = bot.sendImages(images, update);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        deletedMessageCache.put(chatId, messageId);
        bot.send(attribute.getText(), attribute.generateTeacherMarkup(teachers), update);
    }

    private ImageDto getTeacherImage(UUID imageId) {
        if (imageCache.contains(imageId)) {
            return imageCache.get(imageId);
        } else {
            ImageDto image = backendServiceClient.getImageById(imageId);
            imageCache.put(imageId, image);
            return image;
        }
    }
}
