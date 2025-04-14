package ru.pereguzochka.telegram_bot.handler.teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.datatime.GroupDatesAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SelectGroupTeacherHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final BotBackendClient botBackendClient;
    private final GroupTeacherDescriptionAttribute groupTeacherDescriptionAttribute;
    private final GroupDatesAttribute groupDatesAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return  callbackStartWith(update, "/select-group-teacher:");
    }

    @Override
    public void compute(Update update) {
        UUID teacherId = UUID.fromString(getCallbackPayload(update, "/select-group-teacher:"));
        String telegramId = telegramBot.extractTelegramId(update).toString();
        GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
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
        if (teacher.isHidden()) {
            List<GroupTimeSlotDto> timeslots = botBackendClient.getTeacherGroupTimeSlotInNextMonthByLesson(teacherId, lesson.getId());

            String text = groupDatesAttribute.generateText(lesson, teacher);
            InlineKeyboardMarkup markup = groupDatesAttribute.generateDatesMarkup(timeslots, 0);
            telegramBot.edit(text, markup, update);

        } else {
            String text = groupTeacherDescriptionAttribute.generateText(lesson, teacher);
            InlineKeyboardMarkup markup = groupTeacherDescriptionAttribute.createMarkup();

            UUID imageId = teacher.getImageID();
            ImageDto image = botBackendClient.getImageById(imageId);

            telegramBot.delete(update);
            telegramBot.sendImage(image, text, markup, update);
        }

        log.info("telegramId: {} -> /select-group-teacher:{}", telegramId, teacherId);
    }
}
