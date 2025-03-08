package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.DeletedMessageCache;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.datatime_handler.DateAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OneTeacherHandler implements UpdateHandler {
    private final DeletedMessageCache deletedMessageCache;
    private final TelegramBot telegramBot;
    private final BotBackendClient backendClient;
    private final RegistrationCache registrationCache;
    private final DateAttribute dateAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/one-teacher");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        List<Integer> removedMessage = deletedMessageCache.get(telegramId);
        removedMessage.forEach(messageId -> telegramBot.delete(messageId, telegramId));
        UUID teacherId = registrationCache.get(telegramId).getTeacher().getId();

        List<LocalDate> actualDate = backendClient.getTeacherTimeSlotsInNextMonth(teacherId).stream()
                .map(TimeSlotDto::getStartTime)
                .map(LocalDateTime::toLocalDate)
                .toList();

        telegramBot.send(dateAttribute.getText(), dateAttribute.generateLocalDateMarkup(actualDate, 0), update);
    }
}
