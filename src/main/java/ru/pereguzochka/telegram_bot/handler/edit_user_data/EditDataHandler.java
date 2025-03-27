package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedChildByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import static ru.pereguzochka.telegram_bot.dto.UserDto.UserStatus.NEW;

@Component
@RequiredArgsConstructor
public class EditDataHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final UsersByTelegramId usersByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final EditDataAttribute editDataAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/edit-data");
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        ChildDto child = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);
        if (user == null || child == null) {
            restartBotMessageSender.send(update);
            return;
        }

        String text = editDataAttribute.generateEditDataText(user, child);
        InlineKeyboardMarkup markup = editDataAttribute.createMarkup();
        if (user.getStatus().equals(NEW)) {
            markup = editDataAttribute.generateWithPhoneEdit();
        }

        telegramBot.edit(text, markup, update);
    }
}

