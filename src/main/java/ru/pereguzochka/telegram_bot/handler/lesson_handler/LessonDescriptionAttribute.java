package ru.pereguzochka.telegram_bot.handler.lesson_handler;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.lesson-description")
public class LessonDescriptionAttribute extends BaseAttribute {
    public String generateText(LessonDto lesson) {
        return getText().replace("{}", lesson.getName()) + "\n" + lesson.getDescription();
    }
}
