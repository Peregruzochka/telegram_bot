package ru.pereguzochka.telegram_bot.handler.view_registration;

import java.time.LocalDateTime;
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
@ConfigurationProperties(prefix = "attr.view-registration")
@Getter
@Setter
public class ViewAttribute extends BaseAttribute {
    private String emptyRegistrationText;
    private String regPattern;

    public String generateText(List<RegistrationDto> registrations) {
        if (registrations.isEmpty()) {
            return emptyRegistrationText;
        }

        StringBuilder builder = new StringBuilder();
        for (RegistrationDto registration : registrations) {
            builder.append(generateOneRegistrationText(registration));
            builder.append("\n");
        }
        return text.replace("{}", builder.toString());
    }

    private String dateToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMMM, HH:mm", new Locale("ru"));
        return date.format(formatter);
    }

    private String generateOneRegistrationText(RegistrationDto registrations) {
        String date = dateToString(registrations.getSlot().getStartTime());
        String childName = registrations.getChild().getName();
        String lessonName = registrations.getLesson().getName();
        String teacherName = registrations.getTeacher().getName();

        return regPattern
                .replace("{0}", date)
                .replace("{1}", childName)
                .replace("{2}", lessonName)
                .replace("{3}", teacherName);
    }
}
