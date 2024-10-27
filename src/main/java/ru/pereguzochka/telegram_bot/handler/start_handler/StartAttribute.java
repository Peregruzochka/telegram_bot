package ru.pereguzochka.telegram_bot.handler.start_handler;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class StartAttribute {

    private final InlineKeyboardButton buttonOne = InlineKeyboardButton.builder()
            .callbackData("/direction")
            .text("Записаться на занятие")
            .build();

    private final InlineKeyboardButton buttonTwo = InlineKeyboardButton.builder()
            .callbackData("/reschedule")
            .text("Перенести занятие")
            .build();

    private final InlineKeyboardButton buttonThree = InlineKeyboardButton.builder()
            .callbackData("/cancel")
            .text("Отменить занятие")
            .build();

    private final InlineKeyboardButton buttonFour = InlineKeyboardButton.builder()
            .callbackData("/view")
            .text("Посмотреть предстоящие занятия")
            .build();

    @Getter
    private final InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                    List.of(buttonOne),
                    List.of(buttonTwo),
                    List.of(buttonThree),
                    List.of(buttonFour)
            ))
            .build();

    @Getter
    private final String text = "<b>Здравствуйте!</b> \n" +
            "{имя фамилия} \n" +
            "Будем рады помочь в решении Вашего вопроса!\n" +
            "Выберете действие:";
}
