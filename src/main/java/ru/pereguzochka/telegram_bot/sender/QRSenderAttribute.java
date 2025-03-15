package ru.pereguzochka.telegram_bot.sender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.qr-sender")
public class QRSenderAttribute {
    private String text;

    public String generateText(RegistrationEvent event) {
        String date = convertToDate(event.getStartTime());
        String time = convertToTime(event.getStartTime(), event.getEndTime());
        String lesson = event.getLessonName();
        String teacher = event.getTeacherName();
        String child = event.getChildName();

        return text
                .replace("{0}", date)
                .replace("{1}", time)
                .replace("{2}", lesson)
                .replace("{3}", teacher)
                .replace("{4}", child);
    }

    private String convertToDate(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE, d MMMM yyyy", new Locale("ru"));
        return time.format(formatter);
    }

    private String convertToTime(LocalDateTime start, LocalDateTime end) {
        String startTime = start.format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTime = end.format(DateTimeFormatter.ofPattern("HH:mm"));
        return startTime + " - " + endTime;
    }
}
