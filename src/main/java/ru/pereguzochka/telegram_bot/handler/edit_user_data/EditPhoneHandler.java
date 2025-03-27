package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.new_user_input.InputUserPhoneAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputEditPhoneByTelegramId;

@Component
@RequiredArgsConstructor
public class EditPhoneHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final InputEditPhoneByTelegramId inputEditPhoneByTelegramId;
    private final InputUserPhoneAttribute inputUserPhoneAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/edit-phone");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();

        inputEditPhoneByTelegramId.setTrue(telegramId);
        telegramBot.delete(update);

        String text = inputUserPhoneAttribute.getText();
        telegramBot.send(text, update);
    }
}
