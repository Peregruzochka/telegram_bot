package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.cancel")
public class CancelChooseRegistrationAttribute extends BaseAttribute {
    private String cancelCallback;
    private String groupCancelCallback;
    private String emptyRegistrationText;
    private String regPattern;

    public String generateCancelText(List<RegistrationDto> registrations, List<GroupRegistrationDto> groupRegistrations) {
        if (registrations.isEmpty() && groupRegistrations.isEmpty()) {
            return emptyRegistrationText;
        }

        List<Pair<LocalDateTime, String>> pairs = new ArrayList<>();

        registrations.stream()
                .map(this::generateOneRegistrationText)
                .forEach(pairs::add);

        groupRegistrations.stream()
                .map(this::generateOneGroupRegistrationText)
                .forEach(pairs::add);

        List<String> registrationTexts = pairs.stream()
                .sorted(Comparator.comparing(Pair::getFirst))
                .map(Pair::getSecond)
                .toList();

        StringBuilder builder = new StringBuilder();
        registrationTexts.forEach(text -> builder.append(text).append("\n"));

        return text.replace("{}", builder.toString());
    }

    public InlineKeyboardMarkup generateCancelMarkup(List<RegistrationDto> registrations, List<GroupRegistrationDto> groupRegistrations) {
        List<List<InlineKeyboardButton>> newButtons = new ArrayList<>();

        registrations.stream()
                .map(reg -> {
                    String text = dateToString(reg.getSlot().getStartTime()) + " (" + reg.getChild().getName() + ")";
                    String callback = cancelCallback + reg.getId();
                    return createButton(text, callback);
                })
                .map(List::of)
                .forEach(newButtons::add);

        groupRegistrations.stream()
                .map(reg -> {
                    String text = dateToString(reg.getTimeSlot().getStartTime()) + " (" + reg.getChild().getName() + ")";
                    String callback = groupCancelCallback + reg.getId();
                    return createButton(text, callback);
                })
                .map(List::of)
                .forEach(newButtons::add);

        List<List<InlineKeyboardButton>> newSortedButtons = newButtons.stream()
                .sorted(Comparator.comparing(butList -> {
                    InlineKeyboardButton button = butList.get(0);
                    return button.getText();
                }))
                .toList();

        return super.generateMarkup(newSortedButtons);
    }

    private String dateToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMMM, HH:mm", new Locale("ru"));
        return date.format(formatter);
    }

    private Pair<LocalDateTime, String> generateOneRegistrationText(RegistrationDto registrations) {
        LocalDateTime startTime = registrations.getSlot().getStartTime();
        String date = dateToString(startTime);
        String childName = registrations.getChild().getName();
        String lessonName = registrations.getLesson().getName();
        String teacherName = registrations.getTeacher().getName();

        String registrationsText = regPattern
                .replace("{0}", date)
                .replace("{1}", childName)
                .replace("{2}", lessonName)
                .replace("{3}", teacherName);

        return Pair.of(startTime, registrationsText);
    }

    private Pair<LocalDateTime, String> generateOneGroupRegistrationText(GroupRegistrationDto registrations) {
        LocalDateTime startTime = registrations.getTimeSlot().getStartTime();
        String date = dateToString(startTime);
        String childName = registrations.getChild().getName();
        String lessonName = registrations.getTimeSlot().getGroupLesson().getName();
        String teacherName = registrations.getTimeSlot().getTeacher().getName();

        String registrationsText = regPattern
                .replace("{0}", date)
                .replace("{1}", childName)
                .replace("{2}", lessonName)
                .replace("{3}", teacherName);

        return Pair.of(startTime, registrationsText);
    }
}
