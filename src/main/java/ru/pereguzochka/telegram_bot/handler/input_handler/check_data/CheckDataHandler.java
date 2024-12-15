package ru.pereguzochka.telegram_bot.handler.input_handler.check_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class CheckDataHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final RegistrationCache registrationCache;

    @Override
    public boolean isApplicable(Update update) {
        return false;
    }

    @Override
    public void compute(Update update) {

    }
}
