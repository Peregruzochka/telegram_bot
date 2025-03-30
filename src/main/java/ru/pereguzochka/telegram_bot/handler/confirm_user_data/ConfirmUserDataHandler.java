package ru.pereguzochka.telegram_bot.handler.confirm_user_data;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.MainMenuPortAttribute;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

@Component
@RequiredArgsConstructor
public class ConfirmUserDataHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final FinishAttribute finishAttribute;
    private final BotBackendClient botBackendClient;
    private final WrongFinishAttribute wrongFinishAttribute;
    private final TelegramBot telegramBot;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final SelectedTimeSlotByTelegramId selectedTimeSlotByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final MainMenuPortAttribute mainMenuPortAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/confirm-user-data");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();

        LessonDto lesson = selectedLessonByTelegramId.get(telegramId, LessonDto.class).orElse(null);
        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        TimeSlotDto timeslot = selectedTimeSlotByTelegramId.get(telegramId, TimeSlotDto.class).orElse(null);
        ChildDto child = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);
        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);

        if (lesson == null || teacher == null || timeslot == null || child == null || user == null) {
            restartBotMessageSender.send(update);
            return;
        }

        RegistrationDto registrationDto = RegistrationDto.builder()
                .user(user)
                .lesson(lesson)
                .teacher(teacher)
                .slot(timeslot)
                .child(child)
                .build();

        try {
            botBackendClient.addRegistration(registrationDto);
            String text = finishAttribute.generateText(lesson, teacher, timeslot, child);
            bot.delete(update);
            bot.send(text, update);
            String secondText = mainMenuPortAttribute.getText();
            InlineKeyboardMarkup secondMarkup = mainMenuPortAttribute.createMarkup();
            bot.send(secondText, secondMarkup, update);

        } catch (FeignException.InternalServerError e) {
            int httpCode = e.status();
            String responceBody = e.contentUTF8();
            if (httpCode == 500 && responceBody.contains("TimeSlot is not available")) {
                bot.edit(wrongFinishAttribute.getText(), wrongFinishAttribute.createMarkup(), update);
            }
        }
    }
}
