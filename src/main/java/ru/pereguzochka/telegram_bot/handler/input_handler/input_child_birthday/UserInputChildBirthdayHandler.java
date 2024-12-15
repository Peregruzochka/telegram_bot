package ru.pereguzochka.telegram_bot.handler.input_handler.input_child_birthday;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.dto.ChildrenDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.input_handler.input_user_name.InputUserNameAttribute;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class UserInputChildBirthdayHandler implements UpdateHandler {
    private final UserInputFlags userInputFlags;
    private final RegistrationCache registrationCache;
    private final TelegramBot bot;
    private final InputUserNameAttribute inputUserNameAttribute;

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

        Boolean flag = userFlags.get("input-child-birthday");
        return flag != null && flag;
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Map<String, Boolean> userFlags = userInputFlags.getFlags().get(chatId);
        userFlags.put("input-child-birthday", false);

        String userInput = update.getMessage().getText();
        Long telegramId = update.getMessage().getFrom().getId();

        RegistrationDto registrationDto = registrationCache.getCache().get(telegramId);
        ChildrenDto children = registrationDto.getChildren().get(0);
        children.setBirthday(userInput);

        userInputFlags.getFlags().get(chatId).put("input-user-name", true);

        bot.send(inputUserNameAttribute.getText(), update);
    }
}
