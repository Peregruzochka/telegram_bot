package ru.pereguzochka.telegram_bot.handler.start_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.*;

@Component
@RequiredArgsConstructor
public class StartCallbackHandler implements UpdateHandler {

    private final FirstStartAttribute firstStartAttribute;
    private final StartAttribute startAttribute;
    private final TelegramBot bot;
    private final RegistrationCache cache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/start");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = cache.get(telegramId);
        if (registrationDto.getType().equals(NEW_USER)) {
            bot.edit(firstStartAttribute.getText(), firstStartAttribute.createMarkup(), update);
        } else {
            String username = registrationDto.getUser().getName();
            bot.edit(startAttribute.createText(username), startAttribute.createMarkup(), update);
        }
    }
}
