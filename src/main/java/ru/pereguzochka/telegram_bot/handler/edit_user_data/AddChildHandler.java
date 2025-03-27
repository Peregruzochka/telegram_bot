package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.new_user_input.InputChildNameAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputAddChildNameByTelegramId;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddChildHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final InputAddChildNameByTelegramId inputAddChildNameByTelegramId;
    private final InputChildNameAttribute inputChildNameAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/add-child");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputAddChildNameByTelegramId.setTrue(telegramId);

        telegramBot.delete(update);

        String text = inputChildNameAttribute.getText();
        telegramBot.send(text, update);

        log.info("telegram id: {} -> /add-child", telegramId);
    }
}
