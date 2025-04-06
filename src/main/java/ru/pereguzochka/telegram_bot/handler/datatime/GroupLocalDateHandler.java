package ru.pereguzochka.telegram_bot.handler.datatime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedTeacherByTelegramId;
import ru.pereguzochka.telegram_bot.sender.RestartBotMessageSender;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupLocalDateHandler implements UpdateHandler {
    private final TelegramBot telegramBot;
    private final SelectedTeacherByTelegramId selectedTeacherByTelegramId;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final RestartBotMessageSender restartBotMessageSender;
    private final BotBackendClient botBackendClient;
    private final GroupTimeSlotAttribute groupTimeSlotAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/group-local-date:");
    }

    @Override
    public void compute(Update update) {
        LocalDate localDate = LocalDate.parse(getCallbackPayload(update, "/group-local-date:"));
        String telegramId = telegramBot.extractTelegramId(update).toString();

        TeacherDto teacher = selectedTeacherByTelegramId.get(telegramId, TeacherDto.class).orElse(null);
        GroupLessonDto lesson = selectedGroupLessonByTelegramId.get(telegramId, GroupLessonDto.class).orElse(null);
        if (lesson == null || teacher == null) {
            restartBotMessageSender.send(update);
            return;
        }

        List<GroupTimeSlotDto> timeslots = botBackendClient.getAvailableGroupTimeSlotsByDate(teacher.getId(), localDate);
        List<GroupTimeSlotDto> userTimeslots = botBackendClient.getAvailableGroupTimeSlotsByDate(teacher.getId(), localDate);

        String text = groupTimeSlotAttribute.generateText(lesson, teacher, localDate);
        InlineKeyboardMarkup markup = groupTimeSlotAttribute.createTimeMarkup(timeslots, userTimeslots);

        telegramBot.edit(text, markup, update);

        log.info("telegramId: {} -> {}", telegramId, update.getCallbackQuery().getData());
    }
}
