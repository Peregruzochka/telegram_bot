package ru.pereguzochka.telegram_bot.handler.input_handler.input_child_name;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotCache;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.HashMap;
import java.util.UUID;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.NEW_USER;

@Component
@RequiredArgsConstructor
public class NewUserChildNameHandler implements UpdateHandler {
    private final RegistrationCache registrationCache;
    private final TelegramBot bot;
    private final InputChildNameAttribute attribute;
    private final TimeSlotCache timeSlotCache;
    private final UserInputFlags userInputFlags;

    @Override
    public boolean isApplicable(Update update) {
        if (update.hasCallbackQuery()) {
            Long telegramId = update.getCallbackQuery().getFrom().getId();
            return update.getCallbackQuery().getData().startsWith("/time-slot:")
                    && isNewUser(telegramId);
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

        Long chatId = update.getCallbackQuery().getFrom().getId();
        if (!userInputFlags.contains(chatId)) {
            userInputFlags.put(chatId, new HashMap<>());
        }
        userInputFlags.get(chatId).put("input-child-name", true);

        bot.edit(attribute.getText(), update);
    }

    private boolean isNewUser(Long telegramId) {
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        return registrationDto.getType().equals(NEW_USER);
    }
}
