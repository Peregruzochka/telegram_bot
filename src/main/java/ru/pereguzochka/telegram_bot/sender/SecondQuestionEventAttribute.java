package ru.pereguzochka.telegram_bot.sender;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationEvent;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@ConfigurationProperties(prefix = "attr.second-question-event-sender")
public class SecondQuestionEventAttribute extends BaseAttribute {

    public String generateText(RegistrationEvent event) {
        String date = convertToDate(event.getStartTime());
        String time = convertToTime(event.getStartTime(), event.getEndTime());
        String lesson = event.getLessonName();
        String teacher = event.getTeacherName();
        String child = event.getChildName();

        return super.getText()
                .replace("{0}", date)
                .replace("{1}", time)
                .replace("{2}", lesson)
                .replace("{3}", teacher)
                .replace("{4}", child);
    }

    public String generateText(GroupRegistrationEvent event) {
        String date = convertToDate(event.getStartTime());
        String time = convertToTime(event.getStartTime(), event.getEndTime());
        String lesson = event.getLessonName();
        String teacher = event.getTeacherName();
        String child = event.getChildName();

        return super.getText()
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
