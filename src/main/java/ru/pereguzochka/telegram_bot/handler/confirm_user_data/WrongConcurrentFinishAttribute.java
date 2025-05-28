package ru.pereguzochka.telegram_bot.handler.confirm_user_data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.wrong-concurrent-finish")
public class WrongConcurrentFinishAttribute extends BaseAttribute {
    private String childTimeConflictText;
}
