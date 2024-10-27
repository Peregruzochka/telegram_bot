package ru.pereguzochka.telegram_bot.handler.start_handler;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
public class StartAttribute extends BaseAttribute<StartAttributePojo> {

    public StartAttribute(StartAttributePojo attributePojo) {
        super(attributePojo);
    }
}
