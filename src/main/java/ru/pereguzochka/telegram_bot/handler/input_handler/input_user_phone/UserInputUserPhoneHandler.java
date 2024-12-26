package ru.pereguzochka.telegram_bot.handler.input_handler.input_user_phone;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.input_handler.check_data.CheckDataAttribute;
import ru.pereguzochka.telegram_bot.tools.PhoneNumberValidator;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserInputUserPhoneHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final InputUserPhoneAttribute attribute;
    private final UserInputFlags userInputFlags;
    private final PhoneNumberValidator phoneNumberValidator;
    private final CheckDataAttribute checkDataAttribute;
    private final RegistrationCache registrationCache;

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

        Boolean flag = userFlags.get("input-user-phone");
        return flag != null && flag;
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getMessage().getChatId();
        userInputFlags.get(chatId).put("input-user-phone", false);

        String phone = update.getMessage().getText();
        if (phoneNumberValidator.isValidPhoneNumber(phone))  {
            Long telegramId = update.getMessage().getFrom().getId();

            RegistrationDto registrationDto = registrationCache.get(telegramId);
            registrationDto.getUser().setPhone(phone);

            bot.send(checkDataAttribute.generateText(registrationDto), checkDataAttribute.createMarkup(), update);
        } else {
            userInputFlags.get(chatId).put("input-user-phone", true);
            bot.send(attribute.getErrorText(), update);
        }
    }
}
