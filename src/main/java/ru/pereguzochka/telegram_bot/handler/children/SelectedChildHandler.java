package ru.pereguzochka.telegram_bot.handler.children;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.edit_user_data.DataConfirmationAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelectedChildHandler implements UpdateHandler {

    private final UsersByTelegramId usersByTelegramId;
    private final TelegramBot telegramBot;
    private final RestartBotMessageSender restartBotMessageSender;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final SelectedTimeSlotByTelegramId selectedTimeSlotByTelegramId;
    private final DataConfirmationAttribute dataConfirmationAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/selected-child:");
    }

    @Override
    public void compute(Update update) {
        UUID selChildId = UUID.fromString(getCallbackPayload(update, "/selected-child:"));
        String telegramId = telegramBot.extractTelegramId(update).toString();
        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        if (user == null) {
            restartBotMessageSender.send(update);
            return;
        }

        ChildDto selChild = user.getChildren().stream()
                .filter(c -> c.getId().equals(selChildId))
                .findFirst()
                .orElse(null);
        if (selChild == null) {
            restartBotMessageSender.send(update);
            return;
        }
        selectedChildByTelegramId.put(telegramId, selChild);

        LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        TimeSlotDto timeslot = selectedTimeSlotByTelegramId.get(telegramId, TimeSlotDto.class).orElse(null);
        if(lesson == null || teacher == null || timeslot == null) {
            restartBotMessageSender.send(update);
            return;
        }

        String text = dataConfirmationAttribute.generateDataConfirmationText(lesson, teacher, timeslot, selChild, user);
        InlineKeyboardMarkup markup = dataConfirmationAttribute.generateOneMoreChildMarkup();

        telegramBot.edit(text, markup, update);

        log.info("telegramId: {} -> /selected-child:{}", telegramId, selChildId);
    }
}
