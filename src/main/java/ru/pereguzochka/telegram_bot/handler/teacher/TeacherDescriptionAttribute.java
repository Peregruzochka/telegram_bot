package ru.pereguzochka.telegram_bot.handler.teacher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.teacher-description")
public class TeacherDescriptionAttribute extends BaseAttribute {

    public String generateText(LessonDto lesson, TeacherDto teacher) {
        String lessonName = lesson.getName();
        String teacherName = teacher.getName();

        return super.text
                .replace("{0}", lessonName)
                .replace("{1}", teacherName);
    }
}
