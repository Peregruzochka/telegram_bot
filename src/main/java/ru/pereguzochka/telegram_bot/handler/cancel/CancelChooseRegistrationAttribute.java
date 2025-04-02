package ru.pereguzochka.telegram_bot.handler.cancel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.cancel")
public class CancelChooseRegistrationAttribute extends BaseAttribute {
    private String cancelCallback;
    private String emptyRegistrationText;
    private String regPattern;

    public String generateCancelText(List<RegistrationDto> registrations) {
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

    public InlineKeyboardMarkup generateCancelMarkup(List<RegistrationDto> registrations) {
        List<List<InlineKeyboardButton>> newButtons = registrations.stream()
                .map(reg -> {
                    String text = dateToString(reg.getSlot().getStartTime()) + " (" + reg.getChild().getName() + ")";
                    String callback = cancelCallback + reg.getId();
                    return createButton(text, callback);
                })
                .map(List::of)
                .toList();

        return super.generateMarkup(newButtons);
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
