package ru.pereguzochka.telegram_bot.handler.edit_and_confirm_user_data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.edit-data")
public class EditDataAttribute extends BaseAttribute {
    private String editPhoneButton;
    private String editPhoneCallback;

    public String generateEditDataText(UserDto user, ChildDto child) {
        String userName = user.getName();
        String phone = user.getPhone();
        String childName = child.getName();
        String childBirthday = child.getBirthday();

        return text
                .replace("{0}", userName)
                .replace("{1}", phone)
                .replace("{2}", childName)
                .replace("{3}", childBirthday);
    }

    public InlineKeyboardMarkup generateWithPhoneEdit() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>(createMarkup().getKeyboard());
        InlineKeyboardButton phoneButton = createButton(editPhoneButton, editPhoneCallback);
        buttons.add(1, List.of(phoneButton));

        return new InlineKeyboardMarkup(buttons);
    }
}
