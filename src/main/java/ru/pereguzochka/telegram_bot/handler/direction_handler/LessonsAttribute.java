package ru.pereguzochka.telegram_bot.handler.direction_handler;

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
    private String mainCallback;

    public InlineKeyboardMarkup generateMarkup(List<LessonDto> lessons) {
        InlineKeyboardMarkup markup = createMarkup();
        List<List<InlineKeyboardButton>> oldButtons = markup.getKeyboard();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>(lessons.stream()
                .map(lessonDto -> List.of(createButton(lessonDto.getName(), mainCallback + lessonDto.getId())))
                .toList());
        buttons.addAll(oldButtons);
        markup.setKeyboard(buttons);
        return markup;
    }
}
