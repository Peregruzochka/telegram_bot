package ru.pereguzochka.telegram_bot.handler.input_handler.check_data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.UserDtoCache;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CheckDataAfterChooseChildHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final CheckDataAttribute checkDataAttribute;
    private final UserDtoCache userDtoCache;
    private final RegistrationCache registrationCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("/child:");
    }

    @Override
    public void compute(Update update) {
        String callback = update.getCallbackQuery().getData();
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        UUID childId = UUID.fromString(callback.replace("/child:", ""));
        ChildDto childDto = userDtoCache.getCache().get(telegramId).getChildren().stream()
                .filter(child -> child.getId().equals(childId))
                .findFirst()
                .orElseThrow();
        RegistrationDto registrationDto = registrationCache.getCache().get(telegramId);
        registrationDto.setChildren(childDto);

        bot.edit(checkDataAttribute.generateText(registrationDto), checkDataAttribute.createMarkup(), update);
    }
}
