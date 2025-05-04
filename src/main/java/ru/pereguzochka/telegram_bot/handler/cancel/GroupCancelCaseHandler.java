package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationDto;
import ru.pereguzochka.telegram_bot.handler.MainMenuPortAttribute;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.SelectedCancelRegistrationIdByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupCancelCaseHandler implements UpdateHandler {
    private final BotBackendClient backendClient;
    private final CancelFinishAttribute cancelFinishAttribute;
    private final SelectedCancelRegistrationIdByTelegramId selectedCancelRegistrationIdByTelegramId;
    private final TelegramBot telegramBot;
    private final RestartBotMessageSender restartBotMessageSender;
    private final MainMenuPortAttribute mainMenuPortAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/group-cancel-case:");
    }

    @Override
    public void compute(Update update) {
        String cancelCase = getCallbackPayload(update, "/group-cancel-case:");
        String telegramId = telegramBot.extractTelegramId(update).toString();
        UUID registrationId = selectedCancelRegistrationIdByTelegramId.get(telegramId, UUID.class).orElse(null);
        if (registrationId == null) {
            restartBotMessageSender.send(update);
            return;
        }
        GroupRegistrationDto groupRegistrationDto = backendClient.getGroupRegistration(registrationId);

        backendClient.addGroupCancel(registrationId, cancelCase);

        String text = cancelFinishAttribute.generateText(groupRegistrationDto);
        telegramBot.delete(update);
        telegramBot.send(text, update);
        String secondText = mainMenuPortAttribute.getText();
        InlineKeyboardMarkup secondMarkup = mainMenuPortAttribute.createMarkup();
        telegramBot.send(secondText, secondMarkup, update);

        log.info("telegramId: {} -> /group-cancel-case: {}", telegramId, cancelCase);
    }
}
