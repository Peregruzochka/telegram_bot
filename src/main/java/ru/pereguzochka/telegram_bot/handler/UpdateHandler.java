package ru.pereguzochka.telegram_bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

    boolean isApplicable(Update update);

    void compute(Update update);

    default boolean hasCallback(Update update, String value) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals(value);
    }

    default boolean hasMessage(Update update, String value) {
        return update.hasMessage() && update.getMessage().getText().equals(value);
    }

    default boolean hasMessage(Update update, boolean value) {
        return update.hasMessage() && value;
    }

    default boolean callbackStartWith(Update update, String value) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(value);
    }

    default String getCallbackPayload(Update update, String overhead) {
        return update.getCallbackQuery().getData().replace(overhead, "");
    }

}
