package ru.pereguzochka.telegram_bot.handler.input;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.input-user-name")
public class InputUserNameAttribute {
    private String text;
}
