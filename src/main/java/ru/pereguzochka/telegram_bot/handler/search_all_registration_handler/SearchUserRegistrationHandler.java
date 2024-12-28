package ru.pereguzochka.telegram_bot.handler.search_all_registration_handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class SearchUserRegistrationHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final SearchUserRegistrationAttribute searchUserRegistrationAttribute;
    private final BackendServiceClient backendServiceClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/view");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        List<RegistrationDto> registrations = backendServiceClient.getAllUserRegistrations(telegramId);
        bot.edit(searchUserRegistrationAttribute.generateText(registrations), searchUserRegistrationAttribute.createMarkup(), update);
    }
}
