package ru.pereguzochka.telegram_bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

    boolean isApplicable(Update update);

    void compute(Update update);
}
