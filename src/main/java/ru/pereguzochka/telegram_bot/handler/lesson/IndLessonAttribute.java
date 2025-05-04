package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.Comparator;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.ind-lesson")
public class IndLessonAttribute extends BaseAttribute {
    private String individualCallback;

    public InlineKeyboardMarkup generateIndLessonMarkup(List<LessonDto> lessons) {
        List<List<InlineKeyboardButton>> lessonButtons = lessons.stream()
                .sorted(Comparator.comparing(LessonDto::getName))
                .map(lesson -> {
                    String btnText = lesson.getName();
                    String btnCallback = individualCallback + lesson.getId();
                    return createButton(btnText, btnCallback);
                })
                .map(List::of)
                .toList();

        return generateMarkup(lessonButtons);
    }
}
