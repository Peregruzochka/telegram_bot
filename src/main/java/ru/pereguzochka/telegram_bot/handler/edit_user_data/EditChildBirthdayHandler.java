package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.new_user_input.InputChildBirthdayAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputEditChildBirthdayByTelegramId;

@Component
@RequiredArgsConstructor
public class EditChildBirthdayHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final InputEditChildBirthdayByTelegramId inputEditChildBirthdayByTelegramId;
    private final InputChildBirthdayAttribute inputChildBirthdayAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/edit-child-birthday");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();

        inputEditChildBirthdayByTelegramId.setTrue(telegramId);
        telegramBot.delete(update);

        String text = inputChildBirthdayAttribute.getText();
        telegramBot.send(text, update);
    }
}
