package ru.pereguzochka.telegram_bot.handler.teacher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.group-teacher-description")
public class GroupTeacherDescriptionAttribute extends BaseAttribute {

    public String generateText(GroupLessonDto lesson, TeacherDto teacher) {
        String lessonName = lesson.getName();
        String teacherName = teacher.getName();

        return super.text
                .replace("{0}", lessonName)
                .replace("{1}", teacherName);
    }
}
