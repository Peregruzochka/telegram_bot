package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CancelHandler implements UpdateHandler {

    private final TelegramBot bot;
    private final BotBackendClient backendClient;
    private final CancelChooseRegistrationAttribute cancelChooseRegistrationAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/cancel");
    }

    @Override
    public void compute(Update update) {
//        Long telegramId = update.getCallbackQuery().getFrom().getId();
//        UUID userId = userDtoCache.get(telegramId).getId();
//        List<RegistrationDto> userRegistrations = backendClient.getAllUserRegistrations(userId);
//
//        Map<UUID, RegistrationDto> userRegistrationMap = userRegistrations.stream()
//                .collect(Collectors.toMap(RegistrationDto::getId, Function.identity()));
//
//        userRegistrationPoolCache.put(telegramId, userRegistrationMap);
//
//
//        bot.edit(
//                cancelChooseRegistrationAttribute.generateText(userRegistrations),
//                cancelChooseRegistrationAttribute.generateReRegistrationMarkup(userRegistrations),
//                update
//        );
    }
}
