package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.new_user_input.InputChildBirthdayAttribute;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputAddChildBirthdayByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputAddChildNameByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;

import static ru.pereguzochka.telegram_bot.dto.ChildDto.ChildStatus.NEW;

@Component
@RequiredArgsConstructor
public class InputAddChildNameHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final InputAddChildNameByTelegramId inputAddChildNameByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final InputAddChildBirthdayByTelegramId inputAddChildBirthdayByTelegramId;
    private final InputChildBirthdayAttribute inputChildBirthdayAttribute;

    @Override
    public boolean isApplicable(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        boolean value = inputAddChildNameByTelegramId.isTrue(telegramId);
        return hasMessage(update, value);
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputAddChildNameByTelegramId.setFalse(telegramId);

        String addChildName = update.getMessage().getText();
        ChildDto addChild = new ChildDto();
        addChild.setStatus(NEW);
        addChild.setName(addChildName);

        selectedChildByTelegramId.put(telegramId, addChild);

        inputAddChildBirthdayByTelegramId.setTrue(telegramId);

        String text = inputChildBirthdayAttribute.getText();
        telegramBot.send(text, update);
    }
}
