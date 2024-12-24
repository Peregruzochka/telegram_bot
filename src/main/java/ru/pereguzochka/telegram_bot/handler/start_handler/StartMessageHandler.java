package ru.pereguzochka.telegram_bot.handler.start_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.NEW_USER;
import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.REGULAR_USER;

@Component
@RequiredArgsConstructor
public class StartMessageHandler implements UpdateHandler {

    private final FirstStartAttribute firstStartAttribute;
    private final StartAttribute startAttribute;
    private final TelegramBot bot;
    private final RegistrationCache cache;
    private final BackendServiceClient backendServiceClient;

    public boolean isApplicable(Update update) {
        return update.hasMessage() && update.getMessage().getText().equals("/start");
    }

    public void compute(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();
        UserDto userDto = backendServiceClient.getUserByTelegramId(telegramId);
        if (userDto == null) {
            cache.put(telegramId, createRegistrationDtoForNewUser(telegramId));
            bot.send(firstStartAttribute.getText(), firstStartAttribute.createMarkup(), update);
        } else {
            cache.put(telegramId, createRegistrationDtoForRegularUser(userDto));
            bot.send(startAttribute.getText(), startAttribute.createMarkup(), update);
        }
    }

    private RegistrationDto createRegistrationDtoForNewUser(Long telegramId) {
        return RegistrationDto.builder()
                .telegramId(telegramId)
                .type(NEW_USER)
                .build();
    }

    private RegistrationDto createRegistrationDtoForRegularUser(UserDto userDto) {
        return RegistrationDto.builder()
                .telegramId(userDto.getTelegramId())
                .user(userDto)
                .type(REGULAR_USER)
                .phone(userDto.getPhone())
                .build();
    }
}
