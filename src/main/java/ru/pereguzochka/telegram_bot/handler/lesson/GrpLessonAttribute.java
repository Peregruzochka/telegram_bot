package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.Comparator;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.grp-lesson")
public class GrpLessonAttribute extends BaseAttribute {
    private String groupCallback;

    public InlineKeyboardMarkup generateGroupLessonMarkup(List<GroupLessonDto> lessons) {
        List<List<InlineKeyboardButton>> lessonButtons = lessons.stream()
                .sorted(Comparator.comparing(GroupLessonDto::getName))
                .map(lesson -> {
                    String btnText = lesson.getName();
                    String btnCallback = groupCallback + lesson.getId();
                    return createButton(btnText, btnCallback);
                })
                .map(List::of)
                .toList();

        return generateMarkup(lessonButtons);
    }
}
