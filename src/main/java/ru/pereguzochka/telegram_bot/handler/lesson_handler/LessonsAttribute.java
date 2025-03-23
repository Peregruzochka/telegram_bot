package ru.pereguzochka.telegram_bot.handler.lesson_handler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.lessons")
public class LessonsAttribute extends BaseAttribute {
    private String individualCallback;
    private String individualLabel;
    private String groupCallback;
    private String groupLabel;

    public InlineKeyboardMarkup generateLessonsKeyboard(List<LessonDto> lessons) {
        List<List<InlineKeyboardButton>> newButtons = lessons.stream()
                .map(lessonDto -> {
                        String buttonText = lessonDto.getName() + " " + individualLabel;
                        String callback = individualCallback + lessonDto.getId();
                        return createButton(buttonText, callback);
                })
                .map(List::of)
                .toList();

        // групповые занятия

        return generateMarkup(newButtons);
    }
}
