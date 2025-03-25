package ru.pereguzochka.telegram_bot.handler.input;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.InputChildBirthdayByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.InputUserNameByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;


@Component
@RequiredArgsConstructor
public class InputChildBirthdayHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final InputChildBirthdayByTelegramId inputChildBirthdayByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final InputUserNameAttribute inputUserNameAttribute;
    private final InputUserNameByTelegramId inputUserNameByTelegramId;

    @Override
    public boolean isApplicable(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        boolean value = inputChildBirthdayByTelegramId.isTrue(telegramId);
        return hasMessage(update, value);
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputChildBirthdayByTelegramId.setFalse(telegramId);

        String childBirthday = update.getMessage().getText();
        ChildDto newChild = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);
        if (newChild == null) {
            restartBotMessageSender.send(update);
            return;
        }

        newChild.setBirthday(childBirthday);
        selectedChildByTelegramId.put(telegramId, newChild);

        String text = inputUserNameAttribute.getText();
        telegramBot.send(text, update);
        inputUserNameByTelegramId.setTrue(telegramId);
    }
}
