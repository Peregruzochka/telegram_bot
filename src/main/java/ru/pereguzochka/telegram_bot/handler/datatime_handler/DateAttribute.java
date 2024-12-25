package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.date")
@RequiredArgsConstructor
public class DateAttribute extends BaseAttribute {
    private String localDateCallback;

    private String okDay;
    private String notOkDay;

    private String lastWeekButton;
    private String lastWeekCallback;
    private String nextWeekButton;
    private String nextWeekCallback;

    public InlineKeyboardMarkup generateLocalDateMarkup(List<LocalDate> actualLocalDates, int next) {
        List<LocalDate> currentWeek = generateWeek(next);
        List<List<InlineKeyboardButton>> newButtons = new ArrayList<>(currentWeek.stream()
                .map(localDate -> {
                    String buttonText = localDateToString(localDate) + " " + isOkDay(localDate, actualLocalDates);
                    String callback = localDateCallback + " " + localDate;
                    return List.of(createButton(buttonText, callback));
                })
                .toList());

        if (next > 0) {
            newButtons.add(List.of(createButton(lastWeekButton, lastWeekCallback)));
        }

        if (next < 4) {
            newButtons.add(List.of(createButton(nextWeekButton, nextWeekCallback)));
        }

        return generateMarkup(newButtons);
    }

    private List<LocalDate> generateWeek(int next) {
        LocalDate now = LocalDate.now().plusWeeks(next);
        LocalDate mondayDate = now.with(DayOfWeek.MONDAY);
        return IntStream.range(0, 7)
                .mapToObj(mondayDate::plusDays)
                .toList();
    }

    private String isOkDay(LocalDate date, List<LocalDate> actualLocalDates) {
        if (actualLocalDates.contains(date)) {
            return okDay;
        } else {
            return notOkDay;
        }
    }

    private String localDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMMM", new Locale("ru"));
        return date.format(formatter);
    }
}
