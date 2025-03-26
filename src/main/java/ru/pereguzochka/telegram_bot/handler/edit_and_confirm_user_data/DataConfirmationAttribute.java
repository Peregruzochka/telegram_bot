package ru.pereguzochka.telegram_bot.handler.edit_and_confirm_user_data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@ConfigurationProperties(prefix = "attr.data-confirmation")
public class DataConfirmationAttribute extends BaseAttribute {

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
