package ru.pereguzochka.telegram_bot.handler.view_registration;

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

import java.util.List;

@Component
@RequiredArgsConstructor
public class ViewHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final BotBackendClient backendClient;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final ViewAttribute viewAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/view");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        if (user == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<RegistrationDto> registrations = backendClient.getAllUserActualRegistrations(user.getId());
        List<GroupRegistrationDto> groupRegistration = backendClient.getAllUserActualGroupRegistrations(user.getId());

        String text = viewAttribute.generateText(registrations, groupRegistration);
        InlineKeyboardMarkup markup = viewAttribute.createMarkup();

        telegramBot.edit(text, markup, update);
    }
}
