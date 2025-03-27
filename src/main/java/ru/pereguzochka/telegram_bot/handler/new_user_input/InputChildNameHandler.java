package ru.pereguzochka.telegram_bot.handler.new_user_input;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputChildBirthdayByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputChildNameByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

@Component
@RequiredArgsConstructor
public class InputChildNameHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final InputChildNameByTelegramId inputChildNameByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final InputChildBirthdayByTelegramId inputChildBirthdayByTelegramId;
    private final InputChildBirthdayAttribute inputChildBirthdayAttribute;

    @Override
    public boolean isApplicable(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        boolean value = inputChildNameByTelegramId.isTrue(telegramId);
        return hasMessage(update, value);
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputChildNameByTelegramId.setFalse(telegramId);

        String childName = update.getMessage().getText();
        ChildDto childDto = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);

        if (childDto == null) {
            restartBotMessageSender.send(update);
            return;
        }

        childDto.setName(childName);
        selectedChildByTelegramId.put(telegramId, childDto);

        String text = inputChildBirthdayAttribute.getText();
        telegramBot.send(text, update);
        inputChildBirthdayByTelegramId.setTrue(telegramId);
    }
}
