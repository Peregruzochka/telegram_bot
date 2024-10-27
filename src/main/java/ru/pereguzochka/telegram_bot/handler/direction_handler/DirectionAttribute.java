package ru.pereguzochka.telegram_bot.handler.direction_handler;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
public class DirectionAttribute extends BaseAttribute<DirectionAttributePojo> {

    public DirectionAttribute(DirectionAttributePojo attributePojo) {
        super(attributePojo);
    }
}
