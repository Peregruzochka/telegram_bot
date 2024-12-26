package ru.pereguzochka.telegram_bot.handler.input_handler.edityourself;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.input_handler.input_user_name.InputUserNameAttribute;

import java.util.HashMap;


@Component
@RequiredArgsConstructor
public class EditYourselfHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final InputUserNameAttribute inputUserNameAttribute;
    private final UserInputFlags userInputFlags;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/edit-yourself");
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (!userInputFlags.contains(chatId)) {
            userInputFlags.put(chatId, new HashMap<>());
        }
        userInputFlags.get(chatId).put("input-user-name", true);
        bot.edit(inputUserNameAttribute.getText(), inputUserNameAttribute.createMarkup(), update);
    }
}
