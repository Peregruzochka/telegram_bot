package ru.pereguzochka.telegram_bot.handler.datatime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.children.ChooseChildAttribute;
import ru.pereguzochka.telegram_bot.handler.new_user_input.InputChildNameAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputChildNameByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTimeSlotByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;
import java.util.UUID;

import static ru.pereguzochka.telegram_bot.dto.ChildDto.ChildStatus.NEW;

@Component
@RequiredArgsConstructor
public class TimeSlotHandler implements UpdateHandler {

    private final BotBackendClient botBackendClient;
    private final SelectedTimeSlotByTelegramId selectedTimeSlotByTelegramId;
    private final TelegramBot telegramBot;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final ChooseChildAttribute chooseChildAttribute;
    private final InputChildNameAttribute inputChildNameAttribute;
    private final InputChildNameByTelegramId inputChildNameByTelegramId;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/time-slot:");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        UUID timeSlotId = UUID.fromString(update.getCallbackQuery().getData().replace("/time-slot:", ""));

        TimeSlotDto timeSlotDto = botBackendClient.getTimeSlot(timeSlotId);
        selectedTimeSlotByTelegramId.put(telegramId, timeSlotDto);

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
        } else {
            String text = chooseChildAttribute.getText();
            InlineKeyboardMarkup markup = chooseChildAttribute.generateChildMarkup(children);
            telegramBot.edit(text, markup, update);
        }


    }
}
