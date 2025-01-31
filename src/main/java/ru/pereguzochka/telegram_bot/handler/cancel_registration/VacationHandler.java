package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.CancelDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class VacationHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final RegistrationCache registrationCache;
    private final CancelFinishAttribute finishAttribute;
    private final BotBackendClient backendClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/vacation");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        TimeSlotDto timeSlot = registrationDto.getSlot();
        String caseDescription = "Отпуск";
        CancelDto cancelDto = CancelDto.builder()
                .caseDescription(caseDescription)
                .registrationId(registrationDto.getId())
                .build();
        backendClient.addCancel(cancelDto);
        registrationCache.remove(telegramId);
        bot.edit(finishAttribute.generateText(timeSlot), finishAttribute.createMarkup(), update);
    }
}
