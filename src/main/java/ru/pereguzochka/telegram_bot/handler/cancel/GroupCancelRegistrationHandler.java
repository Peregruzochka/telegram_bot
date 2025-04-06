package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedCancelRegistrationIdByTelegramId;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupCancelRegistrationHandler implements UpdateHandler {
    private final SelectedCancelRegistrationIdByTelegramId selectedCancelRegistrationIdByTelegramId;
    private final TelegramBot telegramBot;
    private final BotBackendClient backendClient;
    private final GroupCancelCaseAttribute groupCancelCaseAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/group-cancel-registration:");
    }

    @Override
    public void compute(Update update) {
        UUID cancelRegistrationId = UUID.fromString(getCallbackPayload(update, "/group-cancel-registration:"));
        String telegramId = telegramBot.extractTelegramId(update).toString();
        selectedCancelRegistrationIdByTelegramId.put(telegramId, cancelRegistrationId);
        GroupRegistrationDto registration = backendClient.getGroupRegistration(cancelRegistrationId);

        String text = groupCancelCaseAttribute.generateText(registration);
        InlineKeyboardMarkup markup = groupCancelCaseAttribute.generateCancelCaseMarkup(registration);

        telegramBot.edit(text, markup, update);
    }
}
