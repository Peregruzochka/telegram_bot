package ru.pereguzochka.telegram_bot.handler.search_all_registration_handler;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.search-user-registration")
@Getter
@Setter
public class SearchUserRegistrationAttribute extends BaseAttribute {
    private String emptyRegistrationText;

    public String generateText(List<RegistrationDto> registrations) {
        String text = getText();
        if (registrations == null || registrations.isEmpty()) {
            text = emptyRegistrationText;
            return text;
        }
        StringBuilder textBuilder = new StringBuilder();
        for (RegistrationDto registration : registrations) {
            textBuilder.append("<b>").append(timeSlotToString(registration.getSlot())).append("</b>").append("\n");
            textBuilder.append("Ребенок: ").append("<i>").append(registration.getChild().getName()).append("</i>").append("\n");
            textBuilder.append("Предмет: ").append("<i>").append(registration.getLesson().getName()).append("</i>").append("\n");
            textBuilder.append("Педагог: ").append("<i>").append(registration.getTeacher().getName()).append("</i>").append("\n");
            textBuilder.append("\n");
        }
        String newText = textBuilder.toString();
        return text.replace("{}", newText);
    }

    private String timeSlotToString(TimeSlotDto timeSlotDto) {
        DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("E", Locale.forLanguageTag("ru"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String startDayOfWeek = timeSlotDto.getStartTime().format(dayOfWeekFormatter); // "Пн"
        String startDate = timeSlotDto.getStartTime().format(dateFormatter);           // "14 декабря"
        String startTimeFormatted = timeSlotDto.getStartTime().format(timeFormatter);  // "09:00"
        String endTimeFormatted = timeSlotDto.getEndTime().format(timeFormatter);      // "09:45"

        return String.format("%s, %s, %s - %s", startDayOfWeek, startDate, startTimeFormatted, endTimeFormatted);
    }
}
