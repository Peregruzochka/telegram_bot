package ru.pereguzochka.telegram_bot.handler.not_confirmed;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeclineHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final DeclineMessageAttribute declineMessageAttribute;
    private final BotBackendClient backendClient;


    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/first-decline:");
    }

    @Override
    public void compute(Update update) {
        UUID registrationId = UUID.fromString(getCallbackPayload(update, "/first-decline:"));
        Long telegramId = telegramBot.extractTelegramId(update);
        try {
            backendClient.declineRegistration(registrationId);
            telegramBot.answer(update);
            String text = declineMessageAttribute.getText();
            InlineKeyboardMarkup markup = declineMessageAttribute.createMarkup();
            telegramBot.send(text, markup, update);
            log.info("telegram_id: {} -> /first-decline", telegramId);
        } catch (FeignException.InternalServerError e) {
            if (e.getMessage().contains("Incorrect decline")) {
                telegramBot.answer(update);
                String text = declineMessageAttribute.getIncorrectDeclineText();
                InlineKeyboardMarkup markup = declineMessageAttribute.createMarkup();
                telegramBot.send(text, markup, update);
                log.info("telegram_id: {} -> /incorrect-first-decline", telegramId);
            }
        }
    }
}

