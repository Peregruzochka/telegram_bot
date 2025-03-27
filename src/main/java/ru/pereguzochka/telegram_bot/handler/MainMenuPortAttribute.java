package ru.pereguzochka.telegram_bot.handler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "attr.main-menu-port")
public class MainMenuPortAttribute extends BaseAttribute {
}
