package ru.pereguzochka.telegram_bot.handler.direction_handler;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class DirectionAttribute {

    @Getter
    private final String text = "<b>Выберете желаемое направление</b>";

    private final InlineKeyboardButton buttonOne = InlineKeyboardButton.builder()
            .text("Логопед")
            .callbackData("/dir1")
            .build();

    private final InlineKeyboardButton buttonTwo = InlineKeyboardButton.builder()
            .text("Диагностика с логопедом")
            .callbackData("/dir2")
            .build();

    private final InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("« Назад")
            .callbackData("/start")
            .build();

    @Getter
    private final InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonOne))
            .keyboardRow(List.of(buttonTwo))
            .keyboardRow(List.of(back))
            .build();
}
