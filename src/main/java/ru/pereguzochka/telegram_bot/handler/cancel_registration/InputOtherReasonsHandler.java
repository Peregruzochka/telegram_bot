package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class InputOtherReasonsHandler implements UpdateHandler {
    private final UserInputFlags userInputFlags;
    private final TelegramBot bot;
    private final RegistrationCache registrationCache;
    private final BackendServiceClient backendServiceClient;
    private final CancelFinishAttribute cancelFinishAttribute;

    @Override
    public boolean isApplicable(Update update) {
        if (!update.hasMessage()) {
            return false;
        }

        Long chatId = update.getMessage().getChatId();
        Map<String, Boolean> userFlags = userInputFlags.get(chatId);
        if (userFlags == null) {
            return false;
        }

        Boolean flag = userFlags.get("other-reasons-case");
        return flag != null && flag;
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Map<String, Boolean> userFlags = userInputFlags.get(chatId);
        userFlags.put("other-reasons-case", false);

        Long telegramId = update.getMessage().getFrom().getId();
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        TimeSlotDto timeSlot = registrationDto.getSlot();
        String caseDescription = update.getMessage().getText();
        backendServiceClient.cancelRegistration(registrationDto, caseDescription);
        bot.send(cancelFinishAttribute.generateText(timeSlot), cancelFinishAttribute.createMarkup(), update);
    }
}
