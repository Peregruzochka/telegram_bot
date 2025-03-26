package ru.pereguzochka.telegram_bot.handler.new_user_input;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.InputUserNameByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.InputUserPhoneByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

@Component
@RequiredArgsConstructor
public class InputUserNameHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final InputUserNameByTelegramId inputUserNameByTelegramId;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final InputUserPhoneAttribute inputUserPhoneAttribute;
    private final InputUserPhoneByTelegramId inputUserPhoneByTelegramId;

    @Override
    public boolean isApplicable(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        boolean value = inputUserNameByTelegramId.isTrue(telegramId);
        return hasMessage(update, value);
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputUserNameByTelegramId.setFalse(telegramId);

        UserDto userDto = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        if (userDto == null) {
            restartBotMessageSender.send(update);
            return;
        }

        String userName = update.getMessage().getText();
        userDto.setName(userName);
        usersByTelegramId.put(telegramId, userDto);

        String text = inputUserPhoneAttribute.getText();
        telegramBot.send(text, update);
        inputUserPhoneByTelegramId.setTrue(telegramId);
    }
}
