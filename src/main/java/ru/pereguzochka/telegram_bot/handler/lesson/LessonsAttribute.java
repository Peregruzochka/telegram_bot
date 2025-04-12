package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.ArrayList;
import java.util.Comparator;
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

    public InlineKeyboardMarkup generateLessonsKeyboard(List<LessonDto> lessons, List<GroupLessonDto> groupLessons) {
        List<List<InlineKeyboardButton>> newButtons = new ArrayList<>();

        lessons.stream()
                .map(lessonDto -> {
                        String buttonText = lessonDto.getName() + " " + individualLabel;
                        String callback = individualCallback + lessonDto.getId();
                        return createButton(buttonText, callback);
                })
                .map(List::of)
                .forEach(newButtons::add);

        groupLessons.stream()
                .map(groupLessonDto -> {
                    String buttonText = groupLessonDto.getName() + " " + groupLabel;
                    String callback = groupCallback + groupLessonDto.getId();
                    return createButton(buttonText, callback);
                })
                .map(List::of)
                .forEach(newButtons::add);

        List<List<InlineKeyboardButton>> newSortedButton = newButtons.stream()
                .sorted(Comparator.comparing(list -> list.get(0).getText()))
                .toList();

        return generateMarkup(newSortedButton);
    }
}
