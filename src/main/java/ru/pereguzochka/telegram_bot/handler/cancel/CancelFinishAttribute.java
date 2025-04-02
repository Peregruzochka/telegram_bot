package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;

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
        String data = localDateToString(registration.getSlot());
        String time = timeToString(registration.getSlot());
        String child = registration.getChild().getName();

        return text
                .replace("{0}", lesson)
                .replace("{1}", teacher)
                .replace("{2}", data)
                .replace("{3}", time)
                .replace("{4}", child);
    }

    private String localDateToString(TimeSlotDto timeSlot) {
        LocalDate localDate = timeSlot.getStartTime().toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMMM", new Locale("ru"));
        return localDate.format(formatter);
    }

    private String timeToString(TimeSlotDto timeSlot) {
        LocalDateTime startTime = timeSlot.getStartTime();
        LocalDateTime endTime = timeSlot.getEndTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("ru"));
        return startTime.format(formatter) + " - " + endTime.format(formatter);
    }
}
