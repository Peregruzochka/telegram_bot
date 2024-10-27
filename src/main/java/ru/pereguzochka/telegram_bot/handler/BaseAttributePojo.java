package ru.pereguzochka.telegram_bot.handler;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class BaseAttributePojo {
    private String text;
    private List<String> buttons;
    private List<String> callbacks;
}
