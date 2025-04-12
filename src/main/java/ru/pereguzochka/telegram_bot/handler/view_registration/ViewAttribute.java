package ru.pereguzochka.telegram_bot.handler.view_registration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
@ConfigurationProperties(prefix = "attr.view-registration")
@Getter
@Setter
public class ViewAttribute extends BaseAttribute {
    private String emptyRegistrationText;
    private String regPattern;

    public String generateText(List<RegistrationDto> registrations, List<GroupRegistrationDto> groupRegistration) {
        if (registrations.isEmpty() && groupRegistration.isEmpty()) {
            return emptyRegistrationText;
        }

        return text.replace("{}", generateRegistrationsText(registrations, groupRegistration));
    }

    private String generateRegistrationsText(List<RegistrationDto> registrations, List<GroupRegistrationDto> groupRegistrations) {
        List<Pair<LocalDateTime, String>> pairs = new ArrayList<>();

        registrations.stream()
                .map(this::generateOneRegistrationText)
                .forEach(pairs::add);

        groupRegistrations.stream()
                .map(this::generateOneRegistrationText)
                .forEach(pairs::add);

        StringBuilder builder = new StringBuilder();
        pairs.stream()
                .sorted(Comparator.comparing(Pair::getFirst))
                .map(Pair::getSecond)
                .forEach(regText -> builder.append(regText).append("\n"));

        return builder.toString();
    }

    private String dateToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMMM, HH:mm", new Locale("ru"));
        return date.format(formatter);
    }

    private Pair<LocalDateTime, String> generateOneRegistrationText(GroupRegistrationDto registration) {
        LocalDateTime start = registration.getTimeSlot().getStartTime();
        String date = dateToString(start);
        String childName = registration.getChild().getName();
        String lesson = registration.getTimeSlot().getGroupLesson().getName();
        String teacher = registration.getTimeSlot().getTeacher().getName();

        String regText = regPattern
                .replace("{0}", date)
                .replace("{1}", childName)
                .replace("{2}", lesson)
                .replace("{3}", teacher);

        return Pair.of(start, regText);
    }

    private Pair<LocalDateTime, String> generateOneRegistrationText(RegistrationDto registrations) {
        LocalDateTime start = registrations.getSlot().getStartTime();
        String date = dateToString(start);
        String childName = registrations.getChild().getName();
        String lessonName = registrations.getLesson().getName();
        String teacherName = registrations.getTeacher().getName();

        String regText = regPattern
                .replace("{0}", date)
                .replace("{1}", childName)
                .replace("{2}", lessonName)
                .replace("{3}", teacherName);

        return Pair.of(start, regText);
    }
}
