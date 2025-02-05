package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.cancel-finish")
public class CancelFinishAttribute extends BaseAttribute {

    public String generateText(RegistrationDto registration) {
        String textBuilder = "<b>" + timeSlotToString(registration.getSlot()) + "</b>" + "\n" +
                "Ребенок: " + "<i>" + registration.getChild().getName() + "</i>" + "\n" +
                "Предмет: " + "<i>" + registration.getLesson().getName() + "</i>" + "\n" +
                "Педагог: " + "<i>" + registration.getTeacher().getName() + "</i>" + "\n";
        return super.getText().replace("{}", textBuilder);
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
