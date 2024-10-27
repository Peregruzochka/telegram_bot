package ru.pereguzochka.telegram_bot.handler;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.stream.IntStream;


@RequiredArgsConstructor
public abstract class BaseAttribute<T extends BaseAttributePojo> {

    protected final T attributePojo;

    public String getText() {
        return attributePojo.getText();
    }

    public InlineKeyboardMarkup createMarkup() {
        List<String> buttons = attributePojo.getButtons();
        List<String> callbacks = attributePojo.getCallbacks();

        if (buttons.size() != callbacks.size()) {
            throw new IllegalStateException("The sizes of the lists of buttons and callbacks must match");
        }

        List<List<InlineKeyboardButton>> keyboard = IntStream.range(0, buttons.size())
                .mapToObj(i -> List.of(createButton(buttons.get(i), callbacks.get(i))))
                .toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    protected InlineKeyboardButton createButton(String text, String callback) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callback)
                .build();
    }
}
