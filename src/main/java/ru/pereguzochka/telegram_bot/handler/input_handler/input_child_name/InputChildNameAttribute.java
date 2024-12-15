package ru.pereguzochka.telegram_bot.handler.input_handler.input_child_name;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.input-child-name")
public class InputChildNameAttribute extends BaseAttribute {
}
