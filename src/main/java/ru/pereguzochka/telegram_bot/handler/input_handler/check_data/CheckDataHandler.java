package ru.pereguzochka.telegram_bot.handler.input_handler.check_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotCache;
import ru.pereguzochka.telegram_bot.cache.UserDtoCache;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.NEW_USER;

@Component
@RequiredArgsConstructor
public class CheckDataHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final RegistrationCache registrationCache;
    private final CheckDataAttribute checkDataAttribute;
    private final UserDtoCache userDtoCache;
    private final TimeSlotCache timeSlotCache;

    @Override
    public boolean isApplicable(Update update) {
        if (update.hasCallbackQuery()) {
            Long telegramId = update.getCallbackQuery().getFrom().getId();
            return update.getCallbackQuery().getData().startsWith("/time-slot:")
                    && !isNewUser(telegramId)
                    && userDtoCache.getCache().get(telegramId).getChildren().size() == 1;
        }
        return false;
    }

    @Override
    public void compute(Update update) {
        String timeSlotString = update.getCallbackQuery().getData().replace("/time-slot:", "");
        UUID timeSlotId = UUID.fromString(timeSlotString);
        TimeSlotDto timeSlotDto = timeSlotCache.get(timeSlotId);

        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        registrationDto.setSlot(timeSlotDto);

        bot.edit(checkDataAttribute.generateText(registrationDto), checkDataAttribute.createMarkup(), update);
    }

    private boolean isNewUser(Long telegramId) {
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        return registrationDto.getType().equals(NEW_USER);
    }
}
