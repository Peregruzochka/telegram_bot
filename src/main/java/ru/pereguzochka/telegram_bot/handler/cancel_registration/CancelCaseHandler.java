package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.CancelDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class CancelCaseHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final RegistrationCache registrationCache;
    private final BotBackendClient backendClient;
    private final CancelFinishAttribute cancelFinishAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/cancel-case:");
    }

    @Override
    public void compute(Update update) {
        String cancelCase = update.getCallbackQuery().getData().replace("/cancel-case:", "");
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto= registrationCache.get(telegramId);
        CancelDto cancelDto = CancelDto.builder()
                .registrationId(registrationDto.getId())
                .caseDescription(cancelCase)
                .build();

        backendClient.addCancel(cancelDto);
        bot.edit(
                cancelFinishAttribute.generateText(registrationDto),
                cancelFinishAttribute.createMarkup(),
                update
        );

        registrationCache.remove(telegramId);
    }
}
