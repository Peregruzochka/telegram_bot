package ru.pereguzochka.telegram_bot.handler.input_handler.addchild;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.input_handler.input_child_name.InputChildNameAttribute;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class AddChildHandler implements UpdateHandler {
    private final UserInputFlags userInputFlags;
    private final TelegramBot bot;
    private final InputChildNameAttribute inputChildNameAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/add-child");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        userInputFlags.getFlags().computeIfAbsent(telegramId, k -> new HashMap<>());
        userInputFlags.getFlags().get(telegramId).put("add-child-name", true);
        bot.edit(inputChildNameAttribute.getText(), inputChildNameAttribute.createMarkup(), update);
    }
}
