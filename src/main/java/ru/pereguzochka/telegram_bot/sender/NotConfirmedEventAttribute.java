package ru.pereguzochka.telegram_bot.sender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.not-confirmed-event-sender")
public class NotConfirmedEventAttribute {
    private String text;
    private String confirmButtonText;
    private String declineButtonText;
    private String confirmButtonCallback;
    private String declineButtonCallback;

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

    public InlineKeyboardMarkup generateMarkup(RegistrationEvent event) {
        InlineKeyboardButton buttonOne = createButton(confirmButtonText, confirmButtonCallback + event.getRegistrationId());
        InlineKeyboardButton buttonTwo = createButton(declineButtonText, declineButtonCallback + event.getRegistrationId());
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(buttonOne), List.of(buttonTwo)))
                .build();
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

    private InlineKeyboardButton createButton(String text, String callback) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callback);
        return button;
    }
}
