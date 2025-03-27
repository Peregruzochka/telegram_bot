package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.new_user_input.InputUserPhoneAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputEditPhoneByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;
import ru.pereguzochka.telegram_bot.tools.PhoneNumberFormatter;
import ru.pereguzochka.telegram_bot.tools.PhoneNumberValidator;

import static ru.pereguzochka.telegram_bot.dto.UserDto.UserStatus.NEW;

@Component
@RequiredArgsConstructor
public class InputEditPhoneHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final InputEditPhoneByTelegramId inputEditPhoneByTelegramId;
    private final UsersByTelegramId usersByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final PhoneNumberValidator phoneNumberValidator;
    private final InputUserPhoneAttribute inputUserPhoneAttribute;
    private final PhoneNumberFormatter phoneNumberFormatter;
    private final EditDataAttribute editDataAttribute;

    @Override
    public boolean isApplicable(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        boolean value = inputEditPhoneByTelegramId.isTrue(telegramId);
        return hasMessage(update, value);
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputEditPhoneByTelegramId.setFalse(telegramId);

        UserDto editUser = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        ChildDto child = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);

        if (editUser == null || child == null) {

            restartBotMessageSender.send(update);
            inputEditPhoneByTelegramId.setTrue(telegramId);
            return;
        }

        String editPhone = update.getMessage().getText();

        if (!phoneNumberValidator.isValidPhoneNumber(editPhone)) {
            String text = inputUserPhoneAttribute.getErrorText();
            telegramBot.send(text, update);
            return;
        }

        String formatedEditPhone = phoneNumberFormatter.formatPhoneNumber(editPhone);
        editUser.setPhone(formatedEditPhone);
        usersByTelegramId.put(telegramId, editUser);

        String text = editDataAttribute.generateEditDataText(editUser, child);
        InlineKeyboardMarkup markup = editDataAttribute.createMarkup();
        if (editUser.getStatus().equals(NEW))
            markup = editDataAttribute.generateWithPhoneEdit();

        telegramBot.send(text, markup, update);
    }
}
