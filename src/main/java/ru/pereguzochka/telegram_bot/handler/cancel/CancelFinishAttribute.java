package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.cancel-finish")
public class CancelFinishAttribute {
    private String text;

    public String generateText(RegistrationDto registration) {
        String lesson = registration.getLesson().getName();
        String teacher = registration.getTeacher().getName();
        LocalDateTime start = registration.getSlot().getStartTime();
        LocalDateTime end = registration.getSlot().getEndTime();
        String data = localDateToString(start);
        String time = timeToString(start, end);
        String child = registration.getChild().getName();

        return text
                .replace("{0}", lesson)
                .replace("{1}", teacher)
                .replace("{2}", data)
                .replace("{3}", time)
                .replace("{4}", child);
    }

    public String generateText(GroupRegistrationDto registration) {
        String lesson = registration.getTimeSlot().getGroupLesson().getName();
        String teacher = registration.getTimeSlot().getTeacher().getName();
        LocalDateTime startTime = registration.getTimeSlot().getStartTime();
        LocalDateTime endTime = registration.getTimeSlot().getEndTime();
        String data = localDateToString(startTime);
        String time = timeToString(startTime, endTime);
        String child = registration.getChild().getName();

        return text
                .replace("{0}", lesson)
                .replace("{1}", teacher)
                .replace("{2}", data)
                .replace("{3}", time)
                .replace("{4}", child);
    }

    private String localDateToString(LocalDateTime localDateTime) {
        LocalDate localDate = localDateTime.toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMMM", new Locale("ru"));
        return localDate.format(formatter);
    }

    private String timeToString(LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("ru"));
        return start.format(formatter) + " - " + end.format(formatter);
    }
}
