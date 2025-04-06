package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
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
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.GroupLessonFlagByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputAddChildBirthdayByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

@Component
@RequiredArgsConstructor
public class InputAddChildBirthdayHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final InputAddChildBirthdayByTelegramId inputAddChildBirthdayByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final SelectedTimeSlotByTelegramId selectedTimeSlotByTelegramId;
    private final UsersByTelegramId usersByTelegramId;
    private final DataConfirmationAttribute dataConfirmationAttribute;
    private final GroupLessonFlagByTelegramId groupLessonFlagByTelegramId;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final SelectedGroupTimeSlotByTelegramId selectedGroupTimeSlotByTelegramId;
    private final DataConfirmationGroupAttribute dataConfirmationGroupAttribute;

    @Override
    public boolean isApplicable(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        boolean value = inputAddChildBirthdayByTelegramId.isTrue(telegramId);
        return hasMessage(update, value);
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputAddChildBirthdayByTelegramId.setFalse(telegramId);

        String childBirthday = update.getMessage().getText();
        ChildDto addChild = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);
        if (addChild == null) {
            restartBotMessageSender.send(update);
            return;
        }

        addChild.setBirthday(childBirthday);
        selectedChildByTelegramId.put(telegramId, addChild);

        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        if (groupLessonFlagByTelegramId.isTrue(telegramId)) {
            GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
            GroupTimeSlotDto timeslot = selectedGroupTimeSlotByTelegramId.get(telegramId, GroupTimeSlotDto.class).orElse(null);

            if (lesson == null || teacher == null || timeslot == null || user == null) {
                restartBotMessageSender.send(update);
                return;
            }

            String text = dataConfirmationGroupAttribute.generateDataConfirmationText(lesson, teacher, timeslot, addChild, user);
            InlineKeyboardMarkup markup = dataConfirmationGroupAttribute.createMarkup();
            telegramBot.send(text, markup, update);

        } else {
            LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
            TimeSlotDto timeslot = selectedTimeSlotByTelegramId.get(telegramId, TimeSlotDto.class).orElse(null);

            if (lesson == null || teacher == null || timeslot == null || user == null) {
                restartBotMessageSender.send(update);
                return;
            }

            String text = dataConfirmationAttribute.generateDataConfirmationText(lesson, teacher, timeslot, addChild, user);
            InlineKeyboardMarkup markup = dataConfirmationAttribute.createMarkup();
            telegramBot.send(text, markup, update);
        }



    }
}
