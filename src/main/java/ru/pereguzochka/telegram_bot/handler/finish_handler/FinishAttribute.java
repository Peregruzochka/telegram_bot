package ru.pereguzochka.telegram_bot.handler.finish_handler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.LocalDateTime;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.format.TextStyle.FULL;

@Component
@ConfigurationProperties(prefix = "attr.finish")
public class FinishAttribute  extends BaseAttribute {
    public String generateText(TimeSlotDto timeSlot) {
        LocalDateTime time = timeSlot.getStartTime();
        String dayOfWeek = time.getDayOfWeek().getDisplayName(FULL, new Locale("ru"));
        String dayAndMonth = time.format(ofPattern("d MMMM", new Locale("ru")));
        String hourAndMinute = time.format(ofPattern("HH:mm", new Locale("ru")));
        String date = "<b>" + dayAndMonth + ", " + hourAndMinute + ", " + dayOfWeek + "</b>";
        String text = getText();
        return text.replace("{}", date);
    }
}
