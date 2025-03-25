package ru.pereguzochka.telegram_bot.handler.input;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.InputUserPhoneByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;
import ru.pereguzochka.telegram_bot.tools.PhoneNumberFormatter;
import ru.pereguzochka.telegram_bot.tools.PhoneNumberValidator;

@Component
public class InputUserPhoneHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final InputUserPhoneByTelegramId inputUserPhoneByTelegramId;
    private final PhoneNumberValidator phoneNumberValidator;
    private final PhoneNumberFormatter phoneNumberFormatter;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final InputUserPhoneAttribute inputUserPhoneAttribute;

    public InputUserPhoneHandler(TelegramBot telegramBot, InputUserPhoneByTelegramId inputUserPhoneByTelegramId, PhoneNumberValidator phoneNumberValidator, PhoneNumberFormatter phoneNumberFormatter, UsersByTelegramId usersByTelegramId, RestartBotMessageSender restartBotMessageSender, InputUserPhoneAttribute inputUserPhoneAttribute) {
        this.telegramBot = telegramBot;
        this.inputUserPhoneByTelegramId = inputUserPhoneByTelegramId;
        this.phoneNumberValidator = phoneNumberValidator;
        this.phoneNumberFormatter = phoneNumberFormatter;
        this.usersByTelegramId = usersByTelegramId;
        this.restartBotMessageSender = restartBotMessageSender;
        this.inputUserPhoneAttribute = inputUserPhoneAttribute;
    }

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

            telegramBot.send("FINISH", update);
        } else {
            String text = inputUserPhoneAttribute.getErrorText();
            telegramBot.send(text, update);
            inputUserPhoneByTelegramId.setTrue(telegramId);
        }
    }
}
