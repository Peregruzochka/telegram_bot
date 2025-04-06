package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.GroupLessonFlagByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataConfirmationHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final SelectedTimeSlotByTelegramId selectedTimeSlotByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final DataConfirmationAttribute dataConfirmationAttribute;
    private final GroupLessonFlagByTelegramId groupLessonFlagByTelegramId;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final SelectedGroupTimeSlotByTelegramId selectedGroupTimeSlotByTelegramId;
    private final DataConfirmationGroupAttribute dataConfirmationGroupAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/data-confirmation");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();

        if (groupLessonFlagByTelegramId.isTrue(telegramId)) {
            GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
            TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
            GroupTimeSlotDto timeslot = selectedGroupTimeSlotByTelegramId.get(telegramId, GroupTimeSlotDto.class).orElse(null);
            ChildDto child = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);
            UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);

            if (lesson == null || teacher == null || timeslot == null || child == null || user == null) {
                restartBotMessageSender.send(update);
                return;
            }

            String text = dataConfirmationGroupAttribute.generateDataConfirmationText(lesson, teacher, timeslot, child, user);
            InlineKeyboardMarkup markup = dataConfirmationGroupAttribute.createMarkup();
            if (user.getChildren() != null && user.getChildren().size() > 1) {
                markup = dataConfirmationGroupAttribute.generateOneMoreChildMarkup();
            }
            telegramBot.edit(text, markup, update);

        } else {
            LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
            TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
            TimeSlotDto timeslot = selectedTimeSlotByTelegramId.get(telegramId, TimeSlotDto.class).orElse(null);
            ChildDto child = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);
            UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);

            if (lesson == null || teacher == null || timeslot == null || child == null || user == null) {
                restartBotMessageSender.send(update);
                return;
            }

            String text = dataConfirmationAttribute.generateDataConfirmationText(lesson, teacher, timeslot, child, user);
            InlineKeyboardMarkup markup = dataConfirmationAttribute.createMarkup();
            if (user.getChildren() != null && user.getChildren().size() > 1) {
                markup = dataConfirmationAttribute.generateOneMoreChildMarkup();
            }
            telegramBot.edit(text, markup, update);
        }

        log.info("telegramId: {} -> /data-confirmation", telegramId);

    }
}
