package ru.pereguzochka.telegram_bot.handler.input_handler.input_user_name;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.input_handler.input_user_phone.InputUserPhoneAttribute;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserInputUserNameHandler implements UpdateHandler {
    private final UserInputFlags userInputFlags;
    private final RegistrationCache registrationCache;
    private final TelegramBot bot;
    private final InputUserPhoneAttribute inputUserPhoneAttribute;


    @Override
    public boolean isApplicable(Update update) {
        if (!update.hasMessage()) {
            return false;
        }

        Long chatId = update.getMessage().getChatId();
        Map<String, Boolean> userFlags = userInputFlags.getFlags().get(chatId);
        if (userFlags == null) {
            return false;
        }

        Boolean flag = userFlags.get("input-user-name");
        return flag != null && flag;
    }


    @Override
    public void compute(Update update) {
        Long chatId = update.getMessage().getChatId();
        userInputFlags.getFlags().get(chatId).put("input-user-name", false);

        String userInput = update.getMessage().getText();
        Long telegramId = update.getMessage().getFrom().getId();

        RegistrationDto registrationDto = registrationCache.getCache().get(telegramId);
        registrationDto.setUsername(userInput);

        userInputFlags.getFlags().get(chatId).put("input-user-phone", true);
        bot.send(inputUserPhoneAttribute.getText(), update);
    }
}
