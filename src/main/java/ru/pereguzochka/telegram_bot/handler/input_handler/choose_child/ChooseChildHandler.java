package ru.pereguzochka.telegram_bot.handler.input_handler.choose_child;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserDtoCache;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.NEW_USER;

@Component
@RequiredArgsConstructor
public class ChooseChildHandler implements UpdateHandler {
    private final RegistrationCache registrationCache;
    private final TelegramBot bot;
    private final UserDtoCache userDtoCache;
    private final ChooseChildAttribute chooseChildAttribute;

    @Override
    public boolean isApplicable(Update update) {
        if (update.hasCallbackQuery()) {
            Long telegramId = update.getCallbackQuery().getFrom().getId();
            return update.getCallbackQuery().getData().startsWith("/time-slot:")
                    && !isNewUser(telegramId)
                    && userDtoCache.getCache().get(telegramId).getChildren().size() > 1;
        }
        return false;
    }

    @Override
    public void compute(Update update) {
        String timeSlotString = update.getCallbackQuery().getData().replace("/time-slot:", "");
        UUID timeSlotId = UUID.fromString(timeSlotString);
        RegistrationDto registrationDto = getRegistrationDto(update);
        registrationDto.setSlotId(timeSlotId);
        UserDto userDto = userDtoCache.getCache().get(registrationDto.getTelegramId());

        bot.edit(chooseChildAttribute.getText(), chooseChildAttribute.generateMarkup(userDto.getChildren()), update);
    }

    private boolean isNewUser(Long telegramId) {
        RegistrationDto registrationDto = registrationCache.getCache().get(telegramId);
        return registrationDto.getType().equals(NEW_USER);
    }

    private RegistrationDto getRegistrationDto(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        return registrationCache.getCache().get(telegramId);
    }
}
