package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class OtherReasonsHandler implements UpdateHandler {
    private final UserInputFlags userInputFlags;
    private final TelegramBot bot;
    private final OtherReasonsAttribute otherReasonsAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/other-reasons");
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (!userInputFlags.contains(chatId)) {
            userInputFlags.put(chatId, new HashMap<>());
        }
        userInputFlags.get(chatId).put("other-reasons-case", true);
        bot.edit(otherReasonsAttribute.getText(), otherReasonsAttribute.createMarkup(), update);
    }
}
