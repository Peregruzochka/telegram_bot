package ru.pereguzochka.telegram_bot.handler.start_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.Objects;
import java.util.UUID;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.NEW_USER;
import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.REGULAR_USER;

@Component
@RequiredArgsConstructor
public class StartMessageHandler implements UpdateHandler {

    private final FirstStartAttribute firstStartAttribute;
    private final StartAttribute startAttribute;
    private final TelegramBot bot;
    private final RegistrationCache cache;


    public boolean isApplicable(Update update) {
        return update.hasMessage() && update.getMessage().getText().equals("/start");
    }

    public void compute(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();

        UserDto userDto = getUser(telegramId);
        if (Objects.nonNull(userDto)) {
            String username = userDto.getUsername();
            RegistrationDto registrationDto = RegistrationDto.builder()
                    .telegramId(telegramId)
                    .username(username)
                    .type(REGULAR_USER)
                    .build();

            cache.getCache().put(telegramId, registrationDto);
            bot.send(startAttribute.createText(username), startAttribute.createMarkup(), update);
        } else {
            RegistrationDto registrationDto = RegistrationDto.builder()
                    .telegramId(telegramId)
                    .type(NEW_USER)
                    .build();

            cache.getCache().put(telegramId, registrationDto);
            bot.send(firstStartAttribute.getText(), firstStartAttribute.createMarkup(), update);
        }
    }

    private UserDto getUser(Long telegramId) {
        //TODO запрос в бэкэнд
        return UserDto.builder()
                .id(UUID.randomUUID())
                .telegramId(telegramId)
                .username("John Smith")
                .build();
    }
}
