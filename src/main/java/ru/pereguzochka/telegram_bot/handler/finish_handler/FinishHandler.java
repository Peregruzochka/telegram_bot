package ru.pereguzochka.telegram_bot.handler.finish_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotsCache;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FinishHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final TimeSlotsCache timeSlotsCache;
    private final RegistrationCache registrationCache;
    private final FinishAttribute finishAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/finish");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        UUID slotId = registrationCache.getCache().get(telegramId).getSlotId();
        TimeSlotDto slot = timeSlotsCache.getCache().get(slotId);
        bot.edit(finishAttribute.generateText(slot), finishAttribute.createMarkup(), update);
    }
}
