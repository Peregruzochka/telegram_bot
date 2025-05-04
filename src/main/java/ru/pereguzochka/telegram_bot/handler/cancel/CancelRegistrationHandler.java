package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.CreateAtRegistrationDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedCancelRegistrationIdByTelegramId;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelRegistrationHandler implements UpdateHandler {
    private final CancelCaseAttribute cancelCaseAttribute;
    private final SelectedCancelRegistrationIdByTelegramId selectedCancelRegistrationIdByTelegramId;
    private final TelegramBot telegramBot;
    private final BotBackendClient backendClient;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/cancel-registration:");
    }

    @Override
    public void compute(Update update) {
        UUID cancelRegistrationId = UUID.fromString(getCallbackPayload(update, "/cancel-registration:"));
        String telegramId = telegramBot.extractTelegramId(update).toString();
        selectedCancelRegistrationIdByTelegramId.put(telegramId, cancelRegistrationId);
        RegistrationDto registration = backendClient.getRegistration(cancelRegistrationId);
        CreateAtRegistrationDto createAt = backendClient.getCreatedAt(cancelRegistrationId);

        String text = cancelCaseAttribute.generateText(registration, createAt.getCreatedAt());
        InlineKeyboardMarkup markup = cancelCaseAttribute.generateCancelCaseMarkup(registration, createAt.getCreatedAt());

        telegramBot.edit(text, markup, update);

        log.info("telegramId: {} -> /cancel-registration: {}", telegramId, cancelRegistrationId);
    }
}
