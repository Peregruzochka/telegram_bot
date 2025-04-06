package ru.pereguzochka.telegram_bot.handler.datatime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.group-dates")
public class GroupDatesAttribute extends BaseAttribute {
    private String localDateCallback;

    private String okDay;
    private String notOkDay;

    private String lastWeekButton;
    private String lastWeekCallback;
    private String nextWeekButton;
    private String nextWeekCallback;
    private String emptyWeekButton;
    private String emptyCallback;

    public String generateText(GroupLessonDto lesson, TeacherDto teacher) {
        String lessonName = lesson.getName();
        String teacherName = teacher.getName();

        return super.text
                .replace("{0}", lessonName)
                .replace("{1}", teacherName);
    }

    public InlineKeyboardMarkup generateDatesMarkup(List<GroupTimeSlotDto> timeslots, int next) {
        List<InlineKeyboardButton> navigationButtons = createNavigationButtons(next);
        List<List<InlineKeyboardButton>> nextWeekButtons = createNextWeekButtons(timeslots, next);
        List<List<InlineKeyboardButton>> newButtons = new ArrayList<>();
        newButtons.add(navigationButtons);
        newButtons.addAll(nextWeekButtons);
        return generateMarkup(newButtons);
    }

    private List<InlineKeyboardButton> createNavigationButtons(int next) {
        InlineKeyboardButton lastWeek = createButton(lastWeekButton, lastWeekCallback);
        if (next == 0) {
            lastWeek = createButton(emptyWeekButton, emptyCallback);
        }

        InlineKeyboardButton nextWeek = createButton(nextWeekButton, nextWeekCallback);
        if (next == 3) {
            nextWeek = createButton(emptyWeekButton, emptyCallback);
        }

        return List.of(lastWeek, nextWeek);
    }

    private List<List<InlineKeyboardButton>> createNextWeekButtons(List<GroupTimeSlotDto> timeslots, int next) {
        Set<LocalDate> actualLocalDate = timeslots.stream()
                .map(slot -> slot.getStartTime().toLocalDate())
                .collect(Collectors.toSet());

        return generateWeek(next).stream()
                .map(date -> {
                    String buttonText = localDateToString(date) + " " + isOkDay(date, actualLocalDate);
                    String callbackText = actualLocalDate.contains(date) ? localDateCallback + date : emptyCallback;
                    return createButton(buttonText, callbackText);
                })
                .map(List::of)
                .toList();
    }

    private List<LocalDate> generateWeek(int next) {
        LocalDate now = LocalDate.now().plusWeeks(next);
        LocalDate mondayDate = now.with(DayOfWeek.MONDAY);
        return IntStream.range(0, 7)
                .mapToObj(mondayDate::plusDays)
                .toList();
    }

    private String isOkDay(LocalDate date, Set<LocalDate> actualLocalDates) {
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
