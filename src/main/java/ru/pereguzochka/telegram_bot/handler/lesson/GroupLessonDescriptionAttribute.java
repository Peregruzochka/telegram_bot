package ru.pereguzochka.telegram_bot.handler.lesson;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.group-lesson-description")
public class GroupLessonDescriptionAttribute extends BaseAttribute {
    public String generateText(GroupLessonDto lesson) {
        return text.replace("{}", "👣" + lesson.getName() + "👣" + "\n\n" + lesson.getDescription());
    }
}
