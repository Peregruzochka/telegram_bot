package ru.pereguzochka.telegram_bot.handler.datatime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.children.GroupChooseChildAttribute;
import ru.pereguzochka.telegram_bot.handler.edit_user_data.DataConfirmationGroupAttribute;
import ru.pereguzochka.telegram_bot.handler.new_user_input.InputChildNameAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.GroupLessonFlagByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputChildNameByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;
import java.util.UUID;

import static ru.pereguzochka.telegram_bot.dto.ChildDto.ChildStatus.NEW;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupTimeSlotHandler implements UpdateHandler {

    private final BotBackendClient botBackendClient;
    private final TelegramBot telegramBot;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final InputChildNameAttribute inputChildNameAttribute;
    private final InputChildNameByTelegramId inputChildNameByTelegramId;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final SelectedGroupTimeSlotByTelegramId selectedGroupTimeSlotByTelegramId;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final GroupChooseChildAttribute groupChooseChildAttribute;
    private final GroupLessonFlagByTelegramId groupLessonFlagByTelegramId;
    private final DataConfirmationGroupAttribute dataConfirmationGroupAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/group-time-slot:");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();

        callbackStartWith(update, "/group-time-slot:");
        UUID timeSlotId = UUID.fromString(getCallbackPayload(update, "/group-time-slot:"));
        GroupTimeSlotDto timeslot = botBackendClient.getGroupTimeSlot(timeSlotId);
        selectedGroupTimeSlotByTelegramId.put(telegramId, timeslot);
        groupLessonFlagByTelegramId.setTrue(telegramId);

        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        if (user == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<ChildDto> children = user.getChildren();
        if (children == null || children.isEmpty()) {
            ChildDto newChild = new ChildDto();
            newChild.setStatus(NEW);
            selectedChildByTelegramId.put(telegramId, newChild);

            telegramBot.delete(update);
            inputChildNameByTelegramId.setTrue(telegramId);
            String text = inputChildNameAttribute.getText();
            telegramBot.send(text, update);

        } else if (children.size() == 1) {
            ChildDto child = children.get(0);
            selectedChildByTelegramId.put(telegramId, child);

            GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
            TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);

            if (lesson == null || teacher == null) {
                restartBotMessageSender.send(update);
                return;
            }

            String text = dataConfirmationGroupAttribute.generateDataConfirmationText(lesson, teacher, timeslot, child, user);
            InlineKeyboardMarkup markup = dataConfirmationGroupAttribute.createMarkup();
            telegramBot.edit(text, markup, update);

        } else {
            String text = groupChooseChildAttribute.getText();
            InlineKeyboardMarkup markup = groupChooseChildAttribute.generateChildMarkup(children);
            telegramBot.edit(text, markup, update);
        }

        log.info("telegramId: {} -> /group-time-slot: {}", telegramId, timeslot.getId());
    }
}
