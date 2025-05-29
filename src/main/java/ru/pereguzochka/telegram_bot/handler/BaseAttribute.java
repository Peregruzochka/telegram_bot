package ru.pereguzochka.telegram_bot.handler;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


@Getter
@Setter
public abstract class BaseAttribute {

    protected String text;
    protected List<String> buttons;
    protected List<String> callbacks;

    public InlineKeyboardMarkup createMarkup() {
        checkButtonAndCallbackSizes();
        List<List<InlineKeyboardButton>> keyboard = IntStream.range(0, buttons.size())
                .mapToObj(i -> createButton(buttons.get(i), callbacks.get(i)))
                .map(List::of)
                .toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public InlineKeyboardMarkup createMarkupWithoutButtons(int... index) {
        checkButtonAndCallbackSizes();
        List<List<InlineKeyboardButton>> keyboard = IntStream.range(0, buttons.size())
                .dropWhile(i -> Arrays.stream(index).anyMatch(idx -> i == idx))
                .mapToObj(i -> createButton(buttons.get(i), callbacks.get(i)))
                .map(List::of)
                .toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private void checkButtonAndCallbackSizes() {
        if (buttons.size() != callbacks.size()) {
            throw new IllegalStateException("The sizes of the lists of buttons and callbacks must match");
        }
    }

    public InlineKeyboardMarkup generateMarkup(List<List<InlineKeyboardButton>> newButtons) {
        InlineKeyboardMarkup markup = createMarkup();
        List<List<InlineKeyboardButton>> oldButtons = markup.getKeyboard();
        List<List<InlineKeyboardButton>> newButton = new ArrayList<>(newButtons);
        newButton.addAll(oldButtons);
        markup.setKeyboard(newButton);
        return markup;
    }

    protected InlineKeyboardButton createButton(String text, String callback) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callback)
                .build();
    }
}
