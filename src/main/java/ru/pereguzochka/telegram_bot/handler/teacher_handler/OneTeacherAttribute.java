package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;


@Component
@ConfigurationProperties(prefix = "attr.one-teacher")
public class OneTeacherAttribute extends BaseAttribute {
}
