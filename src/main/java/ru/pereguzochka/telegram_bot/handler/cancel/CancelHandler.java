package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CancelHandler implements UpdateHandler {

    private final TelegramBot bot;
    private final BotBackendClient backendClient;
    private final CancelChooseRegistrationAttribute cancelChooseRegistrationAttribute;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final TelegramBot telegramBot;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/cancel");
    }

    @Override
    public void compute(Update update) {
        String telegramId = bot.extractTelegramId(update).toString();
        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        if (user == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<RegistrationDto> registrations = backendClient.getAllUserActualRegistrations(user.getId());
        List<GroupRegistrationDto> groupRegistrations = backendClient.getAllUserActualGroupRegistrations(user.getId());

        String text = cancelChooseRegistrationAttribute.generateCancelText(registrations, groupRegistrations);
        InlineKeyboardMarkup markup = cancelChooseRegistrationAttribute.generateCancelMarkup(registrations, groupRegistrations);

        telegramBot.edit(text, markup, update);
    }
}
