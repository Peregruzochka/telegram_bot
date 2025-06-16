package ru.pereguzochka.telegram_bot.handler.not_confirmed;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupDeclineHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final DeclineMessageAttribute declineMessageAttribute;
    private final BotBackendClient backendClient;


    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/first-group-decline:");
    }

    @Override
    public void compute(Update update) {
        UUID registrationId = UUID.fromString(getCallbackPayload(update, "/first-group-decline:"));
        try{
            backendClient.declineGroupRegistration(registrationId);
            telegramBot.answer(update);
            String text = declineMessageAttribute.getText();
            InlineKeyboardMarkup markup = declineMessageAttribute.createMarkup();
            telegramBot.send(text, markup, update);
        } catch (FeignException.InternalServerError e) {
            if (e.getMessage().contains("Incorrect decline")) {
                telegramBot.answer(update);
                String text = declineMessageAttribute.getIncorrectDeclineText();
                InlineKeyboardMarkup markup = declineMessageAttribute.createMarkup();
                telegramBot.send(text, markup, update);
            }
        }
    }
}

