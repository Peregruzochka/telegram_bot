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
import ru.pereguzochka.telegram_bot.redis.redis_repository.flag_cache.InputEditChildBirthdayByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import static ru.pereguzochka.telegram_bot.dto.ChildDto.ChildStatus.EDITING;
import static ru.pereguzochka.telegram_bot.dto.ChildDto.ChildStatus.REGULAR;
import static ru.pereguzochka.telegram_bot.dto.UserDto.UserStatus.NEW;

@Component
@RequiredArgsConstructor
public class InputEditChildBirthdayHandler implements UpdateHandler {
    private final InputEditChildBirthdayByTelegramId inputEditChildBirthdayByTelegramId;
    private final TelegramBot telegramBot;
    private final UsersByTelegramId usersByTelegramId;
    private final SelectedChildByTelegramId selectedChildByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final EditDataAttribute editDataAttribute;

    @Override
    public boolean isApplicable(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        boolean value = inputEditChildBirthdayByTelegramId.isTrue(telegramId);
        return hasMessage(update, value);
    }

    @Override
    public void compute(Update update) {
        String telegramId = telegramBot.extractTelegramId(update).toString();
        inputEditChildBirthdayByTelegramId.setFalse(telegramId);

        UserDto user = usersByTelegramId.get(telegramId, UserDto.class).orElse(null);
        ChildDto editChild = selectedChildByTelegramId.get(telegramId, ChildDto.class).orElse(null);

        if (user == null || editChild == null) {
            restartBotMessageSender.send(update);
            return;
        }

        String editChildBirthday = update.getMessage().getText();
        if (editChild.getStatus().equals(REGULAR)) {
            editChild.setStatus(EDITING);
        }
        editChild.setBirthday(editChildBirthday);
        selectedChildByTelegramId.put(telegramId, editChild);

        String text = editDataAttribute.generateEditDataText(user, editChild);
        InlineKeyboardMarkup markup = editDataAttribute.createMarkup();
        if (user.getStatus().equals(NEW))
            markup = editDataAttribute.generateWithPhoneEdit();

        telegramBot.send(text, markup, update);
    }
}
