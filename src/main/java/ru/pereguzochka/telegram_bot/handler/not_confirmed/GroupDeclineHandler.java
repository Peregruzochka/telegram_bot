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

        backendClient.declineGroupRegistration(registrationId);

        telegramBot.answer(update);
        telegramBot.send(
                declineMessageAttribute.getText(),
                declineMessageAttribute.createMarkup(),
                update
        );
    }
}

