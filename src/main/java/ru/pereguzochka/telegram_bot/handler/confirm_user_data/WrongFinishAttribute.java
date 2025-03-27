package ru.pereguzochka.telegram_bot.handler.confirm_user_data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.wrong-finish")
public class WrongFinishAttribute extends BaseAttribute {
}
