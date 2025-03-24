package ru.pereguzochka.telegram_bot.handler.datatime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties("attr.time-slot")
public class TimeSlotAttribute extends BaseAttribute {

    private String timeSlotCallback;

    public String generateText(LessonDto lesson, TeacherDto teacher, LocalDate date) {
        String lessonName = lesson.getName();
        String teacherName = teacher.getName();
        String dateString = localDateToString(date);

        return super.text
                .replace("{0}", lessonName)
                .replace("{1}", teacherName)
                .replace("{2}", dateString);
    }

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

    private String localDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMMM", new Locale("ru"));
        return date.format(formatter);
    }
}
