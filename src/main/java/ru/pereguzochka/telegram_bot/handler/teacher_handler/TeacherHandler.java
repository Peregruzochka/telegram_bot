package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.DeletedMessageCache;
import ru.pereguzochka.telegram_bot.cache.ImageCache;
import ru.pereguzochka.telegram_bot.cache.RegistrationCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotCache;
import ru.pereguzochka.telegram_bot.cache.TimeSlotsByDaysCache;
import ru.pereguzochka.telegram_bot.cache.WeekCursorCache;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.datatime_handler.DateAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeacherHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final TeacherAttribute attribute;
    private final RegistrationCache registrationCache;
    private final ImageCache imageCache;
    private final DeletedMessageCache deletedMessageCache;
    private final BotBackendClient botBackendClient;
    private final DateAttribute dateAttribute;
    private final OneTeacherAttribute oneTeacherAttribute;
    private final TimeSlotsByDaysCache timeSlotsByDaysCache;
    private final WeekCursorCache weekCursorCache;
    private final TimeSlotCache timeSlotCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/teachers");
    }

    @Override
    public void compute(Update update) {
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        RegistrationDto registrationDto = registrationCache.get(telegramId);
        List<TeacherDto> teachers = registrationDto.getLesson().getTeachers();
        List<ImageDto> images = teachers.stream()
                .map(TeacherDto::getImageID)
                .map(this::getTeacherImage)
                .toList();

        weekCursorCache.remove(telegramId);


        if (teachers.size() > 1) {
            bot.delete(update);
            List<Integer> messageId = bot.sendImages(images, update);
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            deletedMessageCache.put(chatId, messageId);
            bot.send(
                    attribute.getText(),
                    attribute.generateTeacherMarkup(teachers),
                    update
            );
        } else if (teachers.size() == 1) {
            UUID teacherId = teachers.get(0).getId();
            registrationCache.get(telegramId).setTeacher(teachers.get(0));

            List<TimeSlotDto> slots = botBackendClient.getTeacherTimeSlotsInNextMonth(teacherId).stream()
                    .filter(slot -> slot.getStartTime().isAfter(LocalDateTime.now().plusHours(3)))
                    .toList();
            slots.forEach(timeSlot -> timeSlotCache.put(timeSlot.getId(), timeSlot));

            Map<LocalDate, List<TimeSlotDto>> timeSlotsByDays = slots.stream()
                    .collect(Collectors.groupingBy(timeSlotDto -> timeSlotDto.getStartTime().toLocalDate()));
            timeSlotsByDaysCache.put(telegramId, timeSlotsByDays);


            if (teachers.get(0).isHidden()) {
                List<LocalDate> actualDate = timeSlotsByDays.keySet().stream().toList();

                bot.edit(
                        dateAttribute.getText(),
                        dateAttribute.generateHideTeacherLocalDateMarkup(actualDate, 0),
                        update
                );

            } else {
                bot.delete(update);
                ImageDto imageDto = images.get(0);
                Integer messageId = bot.sendImage(
                        imageDto,
                        oneTeacherAttribute.getText(),
                        oneTeacherAttribute.createMarkup(),
                        update
                );
                deletedMessageCache.put(telegramId, List.of(messageId));
            }
        }
    }

    private ImageDto getTeacherImage(UUID imageId) {
        if (imageCache.contains(imageId)) {
            return imageCache.get(imageId);
        } else {
            ImageDto image = botBackendClient.getImageById(imageId);
            imageCache.put(imageId, image);
            return image;
        }
    }
}
