package ru.pereguzochka.telegram_bot.handler.start;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.UsersByTelegramId;

import static ru.pereguzochka.telegram_bot.dto.UserDto.UserStatus.NEW;
import static ru.pereguzochka.telegram_bot.dto.UserDto.UserStatus.REGULAR;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartMessageHandler implements UpdateHandler {

    private final FirstStartAttribute firstStartAttribute;
    private final StartAttribute startAttribute;
    private final BotBackendClient botBackendClient;

    private final UsersByTelegramId usersByTelegramId;
    private final TelegramBot telegramBot;

    public boolean isApplicable(Update update) {
        return hasMessage(update, "/start");
    }

    public void compute(Update update) {
        Long telegramId = telegramBot.extractTelegramId(update);

        UserDto userDto = botBackendClient.getUserByTelegramId(telegramId);
        if (userDto == null) {
            userDto = new UserDto();
            userDto.setTelegramId(telegramId);
            userDto.setStatus(NEW);
        }

        usersByTelegramId.put(telegramId.toString(), userDto);

        if (userDto.getStatus() == NEW) {
            telegramBot.send(firstStartAttribute.getText(), firstStartAttribute.createMarkup(), update);
        } else if (userDto.getStatus() == REGULAR) {
            String userName = userDto.getName();
            telegramBot.send(startAttribute.createText(userName), startAttribute.createMarkup(), update);
        }

        log.info("telegramId: {} -> /start: {}", telegramId, userDto.getStatus().toString());
    }
}
