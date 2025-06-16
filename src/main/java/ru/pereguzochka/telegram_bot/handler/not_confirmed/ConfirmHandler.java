package ru.pereguzochka.telegram_bot.handler.not_confirmed;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.handler.MainMenuPortAttribute;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmHandler implements UpdateHandler {

    private final BotBackendClient backendClient;
    private final TelegramBot telegramBot;
    private final ConfirmMessageAttribute confirmMessageAttribute;
    private final MainMenuPortAttribute mainMenuPortAttribute;

    @Value("${qr-code-url}")
    private String qrCodeUrl;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/first-confirm:");
    }

    @Override
    public void compute(Update update) {
        UUID registrationId = UUID.fromString(getCallbackPayload(update, "/first-confirm:"));
        Long telegramId = telegramBot.extractTelegramId(update);
        try {
            backendClient.confirmRegistration(registrationId);
            telegramBot.answer(update);
            telegramBot.sendImage(qrCodeUrl, confirmMessageAttribute.getText(), update);
            telegramBot.send(mainMenuPortAttribute.getText(), mainMenuPortAttribute.createMarkup(), update);
            log.info("telegramId: {} -> /first-confirm", telegramId);
        } catch (FeignException.InternalServerError e) {
            if (e.getMessage().contains("Incorrect confirm")) {
                telegramBot.answer(update);
                String text = confirmMessageAttribute.getIncorrectConfirmText();
                InlineKeyboardMarkup markup = mainMenuPortAttribute.createMarkup();
                telegramBot.send(text, markup, update);
                log.info("telegramId: {} -> /incorrect-first-confirm", telegramId);
            }
        }
    }
}
