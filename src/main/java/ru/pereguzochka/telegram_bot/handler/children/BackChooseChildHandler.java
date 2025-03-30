package ru.pereguzochka.telegram_bot.handler.children;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BackChooseChildHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final ChooseChildAttribute chooseChildAttribute;
    private final UsersByTelegramId usersByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/back-choose-child");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();

        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        if (user == null || user.getChildren() == null || user.getChildren().isEmpty()) {
            restartBotMessageSender.send(update);
            return;
        }

        List<ChildDto> children = user.getChildren();

        String text = chooseChildAttribute.getText();
        InlineKeyboardMarkup markup = chooseChildAttribute.generateChildMarkup(children);
        telegramBot.edit(text, markup, update);
    }
}
