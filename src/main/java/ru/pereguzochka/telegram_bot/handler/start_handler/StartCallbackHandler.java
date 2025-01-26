package ru.pereguzochka.telegram_bot.handler.start_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.NEW_USER;
import static ru.pereguzochka.telegram_bot.dto.RegistrationDto.RegistrationType.REGULAR_USER;

@Component
@RequiredArgsConstructor
public class StartCallbackHandler implements UpdateHandler {
    private final FirstStartAttribute firstStartAttribute;
    private final StartAttribute startAttribute;
    private final TelegramBot bot;
    private final RegistrationCache cache;
    private final BotBackendClient backendClient;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/start");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = cache.get(telegramId);
        if (registrationDto == null) {
            UserDto userDto = backendClient.getUserByTelegramId(telegramId);
            if (userDto == null) {
                bot.edit(firstStartAttribute.getText(), firstStartAttribute.createMarkup(), update);
            } else {
                RegistrationDto newRegistrationDto = createRegistrationDtoForRegularUser(userDto);
                cache.put(telegramId, newRegistrationDto);
                bot.edit(startAttribute.createText(userDto.getName()), startAttribute.createMarkup(), update);
            }
        } else {
            if (registrationDto.getType().equals(REGULAR_USER)) {
                String username = registrationDto.getUser().getName();
                bot.edit(startAttribute.createText(username), startAttribute.createMarkup(), update);
            } else if (registrationDto.getType().equals(NEW_USER)) {
                bot.edit(firstStartAttribute.getText(), firstStartAttribute.createMarkup(), update);
            }
        }
    }

    private RegistrationDto createRegistrationDtoForRegularUser(UserDto userDto) {
        return RegistrationDto.builder()
                .telegramId(userDto.getTelegramId())
                .user(userDto)
                .type(REGULAR_USER)
                .build();
    }
}
