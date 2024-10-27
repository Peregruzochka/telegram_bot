package ru.pereguzochka.telegram_bot.handler.direction_handler.info.logoped;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttributePojo;

@Component
@ConfigurationProperties(prefix = "attr.logoped")
public class LogopedInfoPojo extends BaseAttributePojo {
}
