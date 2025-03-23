package ru.pereguzochka.telegram_bot.sender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.restart-bot-message")
public class RestartBotMessageAttribute {
    private String text;
}
