package ru.pereguzochka.telegram_bot.handler.start;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.ChildDto.ChildStatus;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.dto.UserDto.UserStatus;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.UsersByTelegramId;

import static ru.pereguzochka.telegram_bot.dto.UserDto.UserStatus.NEW;
import static ru.pereguzochka.telegram_bot.dto.UserDto.UserStatus.REGULAR;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartCallbackHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final BotBackendClient botBackendClient;
    private final UsersByTelegramId usersByTelegramId;
    private final FirstStartAttribute firstStartAttribute;
    private final StartAttribute startAttribute;


    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/start");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = telegramBot.extractTelegramId(update);

        UserDto userDto = botBackendClient.getUserByTelegramId(telegramId);
        if (userDto == null) {
            userDto = new UserDto();
            userDto.setTelegramId(telegramId);
            userDto.setStatus(NEW);
        } else {
            userDto.setStatus(UserStatus.REGULAR);
            userDto.getChildren().forEach(childDto -> childDto.setStatus(ChildStatus.REGULAR));
        }

        usersByTelegramId.put(telegramId.toString(), userDto);

        if (userDto.getStatus() == NEW) {
            telegramBot.edit(firstStartAttribute.getText(), firstStartAttribute.createMarkup(), update);
        } else if (userDto.getStatus() == REGULAR) {
            String userName = userDto.getName();
            telegramBot.edit(startAttribute.createText(userName), startAttribute.createMarkup(), update);
        }

        log.info("telegramId: {} -> /start: {}", telegramId, userDto.getStatus().toString());
    }
}
