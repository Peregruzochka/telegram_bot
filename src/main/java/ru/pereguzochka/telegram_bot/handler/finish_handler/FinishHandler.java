package ru.pereguzochka.telegram_bot.handler.finish_handler;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class FinishHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final RegistrationCache registrationCache;
    private final FinishAttribute finishAttribute;
    private final BotBackendClient botBackendClient;
    private final WrongFinishAttribute wrongFinishAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/finish");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        TimeSlotDto slot = registrationDto.getSlot();
        UserDto userDto = registrationDto.getUser();
        userDto.setTelegramId(telegramId);

        try {
            botBackendClient.addRegistration(registrationDto);
            registrationCache.remove(telegramId);
            bot.edit(finishAttribute.generateText(slot), finishAttribute.createMarkup(), update);

        } catch (FeignException.InternalServerError e) {
            int httpCode = e.status();
            String responceBody = e.contentUTF8();
            if (httpCode == 500 && responceBody.contains("TimeSlot is not available")) {
                bot.edit(wrongFinishAttribute.getText(), wrongFinishAttribute.createMarkup(), update);
            }
        }
    }
}
