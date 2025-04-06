package ru.pereguzochka.telegram_bot.handler.teacher;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "attr.teachers")
public class TeachersAttribute extends BaseAttribute {
    private String mainCallbacks;
    private String mainGroupCallbacks;

    public String generateText(LessonDto lesson) {
        String lessonName = lesson.getName();

        return super.text.replace("{}", lessonName);
    }

    public String generateText(GroupLessonDto lesson) {
        String lessonName = lesson.getName();

        return super.text.replace("{}", lessonName);
    }

    public InlineKeyboardMarkup generateTeacherMarkup(List<TeacherDto> teachers) {
        List<List<InlineKeyboardButton>> newButtons = generateTeacherButton(teachers, mainCallbacks);
        return generateMarkup(newButtons);
    }

    public InlineKeyboardMarkup generateGroupTeacherMarkup(List<TeacherDto> teachers) {
        List<List<InlineKeyboardButton>> newButtons = generateTeacherButton(teachers, mainGroupCallbacks);
        return generateMarkup(newButtons);
    }

    private List<List<InlineKeyboardButton>> generateTeacherButton(List<TeacherDto> teachers, String callback) {
        return teachers.stream()
                .map(teacherDto -> List.of(createButton(teacherDto.getName(), callback + teacherDto.getId())))
                .toList();
    }
}

