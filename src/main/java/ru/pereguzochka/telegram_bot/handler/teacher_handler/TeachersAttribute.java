package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

    public String generateText(LessonDto lesson) {
        String lessonName = lesson.getName();
        return super.text.replace("{}", lessonName);
    }

    public InlineKeyboardMarkup generateTeacherMarkup(List<TeacherDto> teachers) {
        List<List<InlineKeyboardButton>> newButtons = teachers.stream()
                .map(teacherDto -> createButton(teacherDto.getName(), mainCallbacks + teacherDto.getId()))
                .map(List::of)
                .toList();

        return generateMarkup(newButtons);
    }
}

