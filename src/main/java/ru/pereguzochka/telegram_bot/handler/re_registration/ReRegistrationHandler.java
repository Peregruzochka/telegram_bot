package ru.pereguzochka.telegram_bot.handler.re_registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.UserRegistrationPoolCache;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReRegistrationHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final ReRegistrationAttribute reRegistrationAttribute;
    private final BackendServiceClient backendServiceClient;
    private final UserRegistrationPoolCache userRegistrationPoolCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/re-registration");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        List<RegistrationDto> registrationDtoList = backendServiceClient.getAllUserRegistrations(telegramId);
        Map<UUID, RegistrationDto> registrationMap = registrationDtoList.stream().
                collect(Collectors.toMap(RegistrationDto::getId, registration -> registration));
        userRegistrationPoolCache.put(telegramId, registrationMap);
        bot.edit(reRegistrationAttribute.generateText(registrationDtoList),
                 reRegistrationAttribute.generateReRegistrationMarkup(registrationDtoList),
                 update);
    }
}
