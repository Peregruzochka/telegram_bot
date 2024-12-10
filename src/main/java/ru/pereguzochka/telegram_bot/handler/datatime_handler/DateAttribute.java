package ru.pereguzochka.telegram_bot.handler.datatime_handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.WeekDay;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.date")
@RequiredArgsConstructor
public class DateAttribute extends BaseAttribute {

    private String prevButtonText;
    private String prevButtonCallback;
    private String nextButtonText;
    private String nextButtonCallback;
    private String timeCallback;
    private String okDay;
    private String notOkDay;

    public InlineKeyboardMarkup createWeekMarkup(List<TimeSlotDto> slots, int week) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>(generateWeek(slots, week).stream()
                .map(day -> {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMMM ", new Locale("ru"));
                        StringBuilder text = new StringBuilder(day.getDay().format(formatter));
                        text.setCharAt(0, (char) ((int) text.charAt(0) - 32));
                        text.append(day.isRegistered() ? okDay : notOkDay);

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                        String dayString = mapper.writeValueAsString(day);

                        String callback = timeCallback + dayString;
                        return createButton(text.toString(), callback);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).map(List::of)
                .toList());

        if (week < 4) {
            buttons.add(List.of(createButton(nextButtonText, nextButtonCallback)));
        }

        if (week > 0) {
            buttons.add(0, List.of(createButton(prevButtonText, prevButtonCallback)));
        }

        InlineKeyboardMarkup markup = createMarkup();
        List<List<InlineKeyboardButton>> oldButtons = markup.getKeyboard();
        buttons.addAll(oldButtons);
        markup.setKeyboard(buttons);
        return markup;
    }

    private List<WeekDay> generateWeek(List<TimeSlotDto> slotDto, int next) {
        List<LocalDate> slots = slotDto.stream().map(TimeSlotDto::getStartTime).map(LocalDateTime::toLocalDate).distinct().toList();

        LocalDate now = LocalDate.now().plusWeeks(next);
        LocalDate mondayDate = now.with(DayOfWeek.MONDAY);
        WeekDay monday = new WeekDay(mondayDate, slots.contains(mondayDate));
        List<WeekDay> currentWeek = new ArrayList<>();
        currentWeek.add(monday);
        for (int i = 1; i < 7; i++) {
            LocalDate date = mondayDate.plusDays(i);
            currentWeek.add(new WeekDay(date, slots.contains(date)));
        }

        return currentWeek;
    }
}
