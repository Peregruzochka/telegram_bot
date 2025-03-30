package ru.pereguzochka.telegram_bot.handler.edit_user_data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.data-confirmation")
public class DataConfirmationAttribute extends BaseAttribute {
    private String backChooseChildCallback;

    public String generateDataConfirmationText(LessonDto lesson, TeacherDto teacher, TimeSlotDto timeSlot, ChildDto child, UserDto user) {
        String lessonName = lesson.getName();
        String teacherName = teacher.getName();
        String date = localDateToString(timeSlot);
        String time = timeToString(timeSlot);
        String childName = child.getName();
        String childBirthday = child.getBirthday();
        String userName = user.getName();
        String userPhone = user.getPhone();

        return text
                .replace("{0}", lessonName)
                .replace("{1}", teacherName)
                .replace("{2}", date)
                .replace("{3}", time)
                .replace("{4}", childName)
                .replace("{5}", childBirthday)
                .replace("{6}", userName)
                .replace("{7}", userPhone);
    }

    public InlineKeyboardMarkup generateOneMoreChildMarkup() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>(createMarkup().getKeyboard());
        InlineKeyboardButton button = buttons.get(3).get(0);
        button.setCallbackData(backChooseChildCallback);
        buttons.remove(3);
        buttons.add(List.of(button));
        return InlineKeyboardMarkup.builder().keyboard(buttons).build();
    }

    private String localDateToString(TimeSlotDto timeSlot) {
        LocalDate localDate = timeSlot.getStartTime().toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMMM", new Locale("ru"));
        return localDate.format(formatter);
    }

    private String timeToString(TimeSlotDto timeSlot) {
        LocalDateTime startTime = timeSlot.getStartTime();
        LocalDateTime endTime = timeSlot.getEndTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("ru"));
        return startTime.format(formatter) + " - " + endTime.format(formatter);
    }





}
