package ru.pereguzochka.telegram_bot.handler.lesson;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.lesson-description")
public class LessonDescriptionAttribute extends BaseAttribute {
    public String generateText(LessonDto lesson) {
        return text
                .replace("{0}", lesson.getName())
                .replace("{1}", lesson.getDescription());
    }
}
