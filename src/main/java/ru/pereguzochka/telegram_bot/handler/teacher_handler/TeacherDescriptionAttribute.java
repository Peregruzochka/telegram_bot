package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.teacher-description")
public class TeacherDescriptionAttribute extends BaseAttribute {

    public String generateText(LessonDto lesson) {
        String lessonName = lesson.getName();
        return super.text.replace("{}", lessonName);
    }
}
