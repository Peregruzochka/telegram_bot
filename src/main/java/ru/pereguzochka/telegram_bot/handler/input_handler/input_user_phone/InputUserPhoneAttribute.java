package ru.pereguzochka.telegram_bot.handler.input_handler.input_user_phone;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.input-user-phone")
@Getter
@Setter
public class InputUserPhoneAttribute extends BaseAttribute {
    private String errorText;
}
