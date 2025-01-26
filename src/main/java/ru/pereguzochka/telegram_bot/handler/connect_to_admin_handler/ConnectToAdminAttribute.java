package ru.pereguzochka.telegram_bot.handler.connect_to_admin_handler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.Objects;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "attr.connect-to-admin")
public class ConnectToAdminAttribute extends BaseAttribute {
    private String vkLink;
    private String tgLink;
    private String waLink;

    @Override
    protected InlineKeyboardButton createButton(String text, String callback) {
        InlineKeyboardButton.InlineKeyboardButtonBuilder inlineKeyboardButton = InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callback);

        if (Objects.equals(callback, "/vk")) {
            inlineKeyboardButton.url(vkLink);
        } else if (Objects.equals(callback, "/tg")) {
            inlineKeyboardButton.url(tgLink);
        } else if (Objects.equals(callback, "/wa")) {
            inlineKeyboardButton.url(waLink);
        }

        return inlineKeyboardButton.build();
    }
}
