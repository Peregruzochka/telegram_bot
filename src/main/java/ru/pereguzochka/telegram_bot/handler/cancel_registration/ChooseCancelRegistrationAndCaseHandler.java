package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserRegistrationPoolCache;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.CANCEL;

@Component
@RequiredArgsConstructor
public class ChooseCancelRegistrationAndCaseHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final UserRegistrationPoolCache userRegistrationPoolCache;
    private final RegistrationCache registrationCache;
    private final CancelCaseAttribute cancelCaseAttribute;


    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/cancel-registration:");
    }

    @Override
    public void compute(Update update) {
        String callback = update.getCallbackQuery().getData();
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        UUID registrationId = UUID.fromString(callback.replace("/cancel-registration:", ""));
        RegistrationDto registrationDto = userRegistrationPoolCache.get(telegramId).get(registrationId);
        registrationDto.setType(CANCEL);

        registrationCache.put(telegramId, registrationDto);

        bot.edit(cancelCaseAttribute.getText(), cancelCaseAttribute.createMarkup(), update);
    }
}
