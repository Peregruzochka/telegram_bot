package ru.pereguzochka.telegram_bot.handler.not_confirmed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.decline-message")
public class DeclineMessageAttribute extends BaseAttribute {
    private String incorrectDeclineText;
}
