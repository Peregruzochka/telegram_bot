package ru.pereguzochka.telegram_bot.handler.input_handler.choose_child;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.choose-child")
public class ChooseChildAttribute extends BaseAttribute {
    private String childCallback;

    public InlineKeyboardMarkup generateChooseChildMarkup(List<ChildDto> children) {
        List<List<InlineKeyboardButton>> newMarkup = new ArrayList<>(children.stream()
                .map(childrenDto -> List.of(createButton(childrenDto.getName(), childCallback + childrenDto.getId())))
                .toList());
        return generateMarkup(newMarkup);
    }
}
