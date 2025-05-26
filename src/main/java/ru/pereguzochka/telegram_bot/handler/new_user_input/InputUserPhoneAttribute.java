package ru.pereguzochka.telegram_bot.handler.new_user_input;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.input-user-phone")
public class InputUserPhoneAttribute {
    private String text;
    private String errorText;
    private String errorPhoneText;
}
