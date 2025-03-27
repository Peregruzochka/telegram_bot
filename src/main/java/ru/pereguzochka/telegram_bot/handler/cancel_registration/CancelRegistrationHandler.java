package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class CancelRegistrationHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final CancelCaseAttribute cancelCaseAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/cancel-registration:");
    }

    @Override
    public void compute(Update update) {
//        UUID registrationId = UUID.fromString(update.getCallbackQuery().getData().replace("/cancel-registration:", ""));
//        Long telegramId = update.getCallbackQuery().getFrom().getId();
//        RegistrationDto registrationDto = userRegistrationPoolCache.get(telegramId).get(registrationId);
//
//        registrationCache.put(telegramId, registrationDto);
//
//        bot.edit(
//                cancelCaseAttribute.generateText(registrationDto),
//                cancelCaseAttribute.generateCancelCaseMarkup(registrationDto),
//                update
//        );
    }
}
