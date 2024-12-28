package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class FamilyReasonHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final RegistrationCache registrationCache;
    private final CancelFinishAttribute finishAttribute;
    private final BackendServiceClient backendServiceClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/family-reasons");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        TimeSlotDto timeSlot = registrationDto.getSlot();
        String caseDescription = "Семейные причины";
        backendServiceClient.cancelRegistration(registrationDto, caseDescription);
        bot.edit(finishAttribute.generateText(timeSlot), finishAttribute.createMarkup(), update);
    }
}
