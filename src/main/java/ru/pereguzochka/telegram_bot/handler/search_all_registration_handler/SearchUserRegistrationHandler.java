package ru.pereguzochka.telegram_bot.handler.search_all_registration_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SearchUserRegistrationHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final SearchUserRegistrationAttribute searchUserRegistrationAttribute;
    private final RegistrationCache registrationCache;
    private final BotBackendClient botBackendClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/view");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        UUID userId = registrationCache.get(telegramId).getUser().getId();
        List<RegistrationDto> registrations = botBackendClient.getAllUserRegistrations(userId);
        bot.edit(searchUserRegistrationAttribute.generateText(registrations), searchUserRegistrationAttribute.createMarkup(), update);
    }
}
