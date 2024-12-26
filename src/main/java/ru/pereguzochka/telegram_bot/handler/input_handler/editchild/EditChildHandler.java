package ru.pereguzochka.telegram_bot.handler.input_handler.editchild;

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
public class EditChildHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final UserInputFlags userInputFlags;
    private final InputChildNameAttribute inputChildNameAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/edit-child");
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (userInputFlags.contains(chatId)) {
            userInputFlags.get(chatId).put("edit-child-name", true);
        }

        bot.edit(inputChildNameAttribute.getText(), inputChildNameAttribute.createMarkup(), update);
    }
}
