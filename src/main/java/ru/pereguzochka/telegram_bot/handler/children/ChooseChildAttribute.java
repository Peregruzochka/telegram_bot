package ru.pereguzochka.telegram_bot.handler.children;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.List;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "attr.choose-child")
public class ChooseChildAttribute extends BaseAttribute {
    private String childCallback;

    public InlineKeyboardMarkup generateChildMarkup(List<ChildDto> children) {
        List<List<InlineKeyboardButton>> childrenButtons = children.stream()
                .map(child -> createButton(child.getName(), childCallback + child.getId()))
                .map(List::of)
                .toList();

        return generateMarkup(childrenButtons);
    }

}
