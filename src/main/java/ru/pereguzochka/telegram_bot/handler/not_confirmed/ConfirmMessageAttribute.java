package ru.pereguzochka.telegram_bot.handler.not_confirmed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.confirm-message")
public class ConfirmMessageAttribute {
    private String text;
    private String incorrectConfirmText;
}
