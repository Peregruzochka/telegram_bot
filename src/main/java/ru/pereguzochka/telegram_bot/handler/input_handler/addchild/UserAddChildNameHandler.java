package ru.pereguzochka.telegram_bot.handler.input_handler.addchild;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserInputFlags;
import ru.pereguzochka.telegram_bot.dto.ChildrenDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.input_handler.input_child_birthday.InputChildBirthdayAttribute;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserAddChildNameHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final UserInputFlags userInputFlags;
    private final RegistrationCache registrationCache;
    private final InputChildBirthdayAttribute childBirthdayAttribute;

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

        Boolean flag = userFlags.get("add-child-name");
        return flag != null && flag;
    }

    @Override
    public void compute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Map<String, Boolean> userFlags = userInputFlags.getFlags().get(chatId);
        userFlags.put("add-child-name", false);

        String userInput = update.getMessage().getText();
        Long telegramId = update.getMessage().getFrom().getId();

        RegistrationDto registrationDto = registrationCache.getCache().get(telegramId);
        ChildrenDto childrenDto = ChildrenDto.builder()
                .name(userInput)
                .build();
        registrationDto.setChildren(childrenDto);

        userInputFlags.getFlags().get(chatId).put("edit-child-birthday", true);

        bot.send(childBirthdayAttribute.getText(), update);
    }
}
