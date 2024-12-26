package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
@ConfigurationProperties("attr.time-set")
public class TimeAttribute extends BaseAttribute {

    private String timeSlotCallback;

    public InlineKeyboardMarkup createTimeMarkup(List<TimeSlotDto> timeSlots) {
        List<List<InlineKeyboardButton>> newKeyboard = new ArrayList<>(timeSlots.stream()
                .map(timeSlotDto -> {
                    String buttonText = createTimeText(timeSlotDto);
                    String callbackText = timeSlotCallback + timeSlotDto.getId();
                    return List.of(createButton(buttonText, callbackText));
                }).toList());
        return generateMarkup(newKeyboard);
    }

    private String createTimeText(TimeSlotDto timeSlotDto) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        String startTime = timeSlotDto.getStartTime().format(FORMATTER);
        String endTime = timeSlotDto.getEndTime().format(FORMATTER);
        return startTime + " - " + endTime;
    }
}
