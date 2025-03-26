package ru.pereguzochka.telegram_bot.handler.new_user_input;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.input-child-name")
public class InputChildNameAttribute {
    private String text;
}
