package ru.pereguzochka.telegram_bot.handler.confirm_user_data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.finish")
public class FinishAttribute {
    private  String earlyRegistrationText;
    private  String lateRegistrationText;

    public String generateText(LessonDto lesson, TeacherDto teacher, TimeSlotDto timeSlot, ChildDto child) {
        String lessonName = lesson.getName();
        String teacherName = teacher.getName();
        String date = localDateToString(timeSlot);
        String time = timeToString(timeSlot);
        String childName = child.getName();

        LocalDateTime startTime = timeSlot.getStartTime();
        String outputText = LocalDateTime.now().plusDays(1).isAfter(startTime) ? lateRegistrationText : earlyRegistrationText;
        return outputText
                .replace("{0}", lessonName)
                .replace("{1}", teacherName)
                .replace("{2}", date)
                .replace("{3}", time)
                .replace("{4}", childName);
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

    public String generateText(GroupLessonDto lesson, TeacherDto teacher, GroupTimeSlotDto timeSlot, ChildDto child) {
        String lessonName = lesson.getName();
        String teacherName = teacher.getName();
        String date = localDateToString(timeSlot);
        String time = timeToString(timeSlot);
        String childName = child.getName();

        LocalDateTime startTime = timeSlot.getStartTime();
        String outputText = LocalDateTime.now().plusDays(1).isAfter(startTime) ? lateRegistrationText : earlyRegistrationText;
        return outputText
                .replace("{0}", lessonName)
                .replace("{1}", teacherName)
                .replace("{2}", date)
                .replace("{3}", time)
                .replace("{4}", childName);
    }

    private String localDateToString(GroupTimeSlotDto timeSlot) {
        LocalDate localDate = timeSlot.getStartTime().toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMMM", new Locale("ru"));
        return localDate.format(formatter);
    }

    private String timeToString(GroupTimeSlotDto timeSlot) {
        LocalDateTime startTime = timeSlot.getStartTime();
        LocalDateTime endTime = timeSlot.getEndTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("ru"));
        return startTime.format(formatter) + " - " + endTime.format(formatter);
    }
}
