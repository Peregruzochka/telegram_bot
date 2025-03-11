package ru.pereguzochka.telegram_bot.handler.not_confirmed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class FirstConfirmHandler implements UpdateHandler {

    private final BotBackendClient backendClient;
    private final TelegramBot telegramBot;
    private final ConfirmMessageAttribute confirmMessageAttribute;


    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/first-confirm:");
    }

    @Override
    public void compute(Update update) {
        UUID registrationId = UUID.fromString(update.getCallbackQuery().getData().replace("/first-confirm:", ""));
        backendClient.confirmRegistration(registrationId);

        telegramBot.answer(update);
        telegramBot.send(
                confirmMessageAttribute.getText(),
                confirmMessageAttribute.createMarkup(),
                update
        );
    }
}
