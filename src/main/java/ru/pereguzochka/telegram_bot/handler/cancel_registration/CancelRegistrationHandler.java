package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserRegistrationPoolCache;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.CANCEL;

@Component
@RequiredArgsConstructor
public class CancelRegistrationHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final UserRegistrationPoolCache userRegistrationPoolCache;
    private final CancelCaseAttribute cancelCaseAttribute;
    private final RegistrationCache registrationCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/cancel-registration:");
    }

    @Override
    public void compute(Update update) {
        UUID registrationId = UUID.fromString(update.getCallbackQuery().getData().replace("/cancel-registration:", ""));
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = userRegistrationPoolCache.get(telegramId).get(registrationId);

        registrationCache.put(telegramId, registrationDto);

        bot.edit(
                cancelCaseAttribute.generateText(registrationDto),
                cancelCaseAttribute.generateCancelCaseMarkup(registrationDto),
                update
        );
    }
}
