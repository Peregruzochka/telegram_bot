package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.new_user_input.InputUserNameAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputEditUserNameByTelegramId;

@Component
@RequiredArgsConstructor
public class EditUserNameHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final InputEditUserNameByTelegramId inputEditUserNameByTelegramId;
    private final InputUserNameAttribute inputUserNameAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/edit-user-name");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();

        inputEditUserNameByTelegramId.setTrue(telegramId);
        telegramBot.delete(update);

        String text = inputUserNameAttribute.getText();
        telegramBot.send(text, update);
    }
}
