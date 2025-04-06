package ru.pereguzochka.telegram_bot.handler.new_user_input;

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
import ru.pereguzochka.telegram_bot.handler.edit_user_data.DataConfirmationAttribute;
import ru.pereguzochka.telegram_bot.handler.edit_user_data.DataConfirmationGroupAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.GroupLessonFlagByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputUserPhoneByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;
import ru.pereguzochka.telegram_bot.tools.PhoneNumberFormatter;
import ru.pereguzochka.telegram_bot.tools.PhoneNumberValidator;

@Component
@RequiredArgsConstructor
public class InputUserPhoneHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final InputUserPhoneByTelegramId inputUserPhoneByTelegramId;
    private final PhoneNumberValidator phoneNumberValidator;
    private final PhoneNumberFormatter phoneNumberFormatter;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final InputUserPhoneAttribute inputUserPhoneAttribute;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final SelectedTimeSlotByTelegramId selectedTimeSlotByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final DataConfirmationAttribute dataConfirmationAttribute;
    private final GroupLessonFlagByTelegramId groupLessonFlagByTelegramId;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final SelectedGroupTimeSlotByTelegramId selectedGroupTimeSlotByTelegramId;
    private final DataConfirmationGroupAttribute dataConfirmationGroupAttribute;

    @Override
    public boolean isApplicable(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        boolean value = inputUserPhoneByTelegramId.isTrue(telegramId);
        return hasMessage(update, value);
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputUserPhoneByTelegramId.setFalse(telegramId);

        String phone = update.getMessage().getText();
        if (phoneNumberValidator.isValidPhoneNumber(phone)) {
            String editedPhone = phoneNumberFormatter.formatPhoneNumber(phone);

            UserDto newUser = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
            if (newUser == null) {
                restartBotMessageSender.send(update);
                return;
            }

            newUser.setPhone(editedPhone);
            usersByTelegramId.put(telegramId, newUser);


            TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
            ChildDto child = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);

            if (groupLessonFlagByTelegramId.isTrue(telegramId)) {
                GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
                GroupTimeSlotDto timeslot = selectedGroupTimeSlotByTelegramId.get(telegramId, GroupTimeSlotDto.class).orElse(null);
                if (lesson == null || teacher == null || timeslot == null || child == null) {
                    restartBotMessageSender.send(update);
                    return;
                }

                String text = dataConfirmationGroupAttribute.generateDataConfirmationText(lesson, teacher, timeslot, child, newUser);
                InlineKeyboardMarkup markup = dataConfirmationGroupAttribute.createMarkup();

                telegramBot.send(text, markup, update);
            } else {
                LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
                TimeSlotDto timeslot = selectedTimeSlotByTelegramId.get(telegramId, TimeSlotDto.class).orElse(null);

                if (lesson == null || teacher == null || timeslot == null || child == null) {
                    restartBotMessageSender.send(update);
                    return;
                }

                String text = dataConfirmationAttribute.generateDataConfirmationText(lesson, teacher, timeslot, child, newUser);
                InlineKeyboardMarkup markup = dataConfirmationAttribute.createMarkup();

                telegramBot.send(text, markup, update);
            }

        } else {
            String text = inputUserPhoneAttribute.getErrorText();
            telegramBot.send(text, update);
            inputUserPhoneByTelegramId.setTrue(telegramId);
        }
    }
}
