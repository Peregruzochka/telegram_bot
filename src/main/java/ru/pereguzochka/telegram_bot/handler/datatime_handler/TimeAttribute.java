package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("attr.time-set")
@RequiredArgsConstructor
public class TimeAttribute extends BaseAttribute {

    private String timeSlotCallback;

    public InlineKeyboardMarkup createTimeMarkup(List<TimeSlotDto> timeSlots) {
        List<List<InlineKeyboardButton>> oldKeyboard = createMarkup().getKeyboard();
        List<List<InlineKeyboardButton>> newKeyboard = new ArrayList<>(timeSlots.stream()
                .map(timeSlotDto -> {
                    String buttonText = createTimeText(timeSlotDto);
                    String callbackText = timeSlotCallback + timeSlotDto;
                    return List.of(createButton(buttonText, callbackText));
                }).toList());
        newKeyboard.addAll(oldKeyboard);
        return new InlineKeyboardMarkup(newKeyboard);
    }

    private String createTimeText(TimeSlotDto timeSlotDto) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        String startTime = timeSlotDto.getStartTime().format(FORMATTER);
        String endTime = timeSlotDto.getEndTime().format(FORMATTER);
        return startTime + " - " + endTime;
    }
}
