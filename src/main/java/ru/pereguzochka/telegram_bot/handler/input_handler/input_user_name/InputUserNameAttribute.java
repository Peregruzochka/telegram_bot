package ru.pereguzochka.telegram_bot.handler.input_handler.input_user_name;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.input-user-name")
public class InputUserNameAttribute extends BaseAttribute {
}
