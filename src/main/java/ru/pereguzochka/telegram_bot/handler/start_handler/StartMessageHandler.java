package ru.pereguzochka.telegram_bot.handler.start_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserDtoCache;
import ru.pereguzochka.telegram_bot.dto.ChildrenDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.ArrayList;
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
    private final UserDtoCache userDtoCache;


    public boolean isApplicable(Update update) {
        return update.hasMessage() && update.getMessage().getText().equals("/start");
    }

    public void compute(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();

        UserDto userDto = getUser(telegramId);
        userDtoCache.getCache().put(telegramId, userDto);

        if (Objects.nonNull(userDto)) {
            String username = userDto.getUsername();
            RegistrationDto registrationDto = RegistrationDto.builder()
                    .telegramId(telegramId)
                    .username(username)
                    .type(REGULAR_USER)
                    .children(userDto.getChildren().get(0))
                    .phone(userDto.getPhone())
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

        bot.delete(update);
    }

    private UserDto getUser(Long telegramId) {
        //TODO запрос в бэкэнд
        return UserDto.builder()
                .id(UUID.randomUUID())
                .telegramId(telegramId)
                .username("Елена")
                .phone("+79113025420")
                .children(new ArrayList<>(){{

                    add(ChildrenDto.builder()
                            .id(UUID.randomUUID())
                            .name("Виктор")
                            .birthday("Январь 2017")
                            .build());

                    add(ChildrenDto.builder()
                            .id(UUID.randomUUID())
                            .name("Филлип")
                            .birthday("Сентябрь 2020")
                            .build());
                }})
                .build();
//        return null;
    }
}
