package ru.pereguzochka.telegram_bot.handler.re_registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserRegistrationPoolCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
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
    private final BotBackendClient botBackendClient;
    private final UserRegistrationPoolCache userRegistrationPoolCache;
    private final RegistrationCache registrationCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/re-registration");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        UUID userId = registrationCache.get(telegramId).getUser().getId();
        List<RegistrationDto> registrationDtoList = botBackendClient.getAllUserRegistrations(userId);
        Map<UUID, RegistrationDto> registrationMap = registrationDtoList.stream().
                collect(Collectors.toMap(RegistrationDto::getId, registration -> registration));
        userRegistrationPoolCache.put(telegramId, registrationMap);
        bot.edit(reRegistrationAttribute.generateText(registrationDtoList),
                 reRegistrationAttribute.generateReRegistrationMarkup(registrationDtoList),
                 update);
    }
}
