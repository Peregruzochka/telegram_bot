package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.choose-case")
public class CancelCaseAttribute extends BaseAttribute {

}
