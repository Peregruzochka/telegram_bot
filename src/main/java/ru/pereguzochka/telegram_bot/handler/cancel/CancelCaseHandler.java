package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.CancelDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.MainMenuPortAttribute;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedCancelRegistrationIdByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CancelCaseHandler implements UpdateHandler {
    private final BotBackendClient backendClient;
    private final CancelFinishAttribute cancelFinishAttribute;
    private final SelectedCancelRegistrationIdByTelegramId selectedCancelRegistrationIdByTelegramId;
    private final TelegramBot telegramBot;
    private final RestartBotMessageSender restartBotMessageSender;
    private final MainMenuPortAttribute mainMenuPortAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/cancel-case:");
    }

    @Override
    public void compute(Update update) {
        String cancelCase = getCallbackPayload(update, "/cancel-case:");
        String telegramId = telegramBot.extractTelegramId(update).toString();
        UUID registrationId = selectedCancelRegistrationIdByTelegramId.get(telegramId, UUID.class).orElse(null);
        if (registrationId == null) {
            restartBotMessageSender.send(update);
            return;
        }
        RegistrationDto registration = backendClient.getRegistration(registrationId);

        CancelDto cancel = CancelDto.builder()
                .registrationId(registrationId)
                .caseDescription(cancelCase)
                .build();

        backendClient.addCancel(cancel);

        String text = cancelFinishAttribute.generateText(registration);
        telegramBot.delete(update);
        telegramBot.send(text, update);
        String secondText = mainMenuPortAttribute.getText();
        InlineKeyboardMarkup secondMarkup = mainMenuPortAttribute.createMarkup();
        telegramBot.send(secondText, secondMarkup, update);
    }
}
