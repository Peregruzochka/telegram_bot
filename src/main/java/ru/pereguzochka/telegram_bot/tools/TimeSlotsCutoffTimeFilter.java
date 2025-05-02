package ru.pereguzochka.telegram_bot.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TimeSlotsCutoffTimeFilter {

    @Value("${cut-off-choose-time}")
    private int cutOffTime;

    public List<TimeSlotDto> cutOff(List<TimeSlotDto> timeslots) {
        return timeslots.stream()
                .filter(this::isValid)
                .toList();
    }

    public List<GroupTimeSlotDto> cutOffGroup(List<GroupTimeSlotDto> timeslots) {
        return timeslots.stream()
                .filter(this::isValid)
                .toList();
    }

    private boolean isValid(TimeSlotDto timeslot) {
        LocalDateTime time = timeslot.getStartTime();
        return LocalDateTime.now().plusHours(cutOffTime).isBefore(time);
    }

    private boolean isValid(GroupTimeSlotDto timeSlot) {
        LocalDateTime time = timeSlot.getStartTime();
        return LocalDateTime.now().plusHours(cutOffTime).isBefore(time);
    }

}
