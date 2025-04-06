package ru.pereguzochka.telegram_bot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.pereguzochka.telegram_bot.dto.CancelDto;
import ru.pereguzochka.telegram_bot.dto.CreateAtRegistrationDto;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationDto;
import ru.pereguzochka.telegram_bot.dto.GroupTimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "bot-backend", url = "${bot-backend.host}:${bot-backend.port}")
public interface BotBackendClient {
    @GetMapping("/users")
    UserDto getUserByTelegramId(@RequestParam(name = "telegram-id") Long telegramId);

    @GetMapping("/lessons/all")
    List<LessonDto> getAllLessons();

    @GetMapping("lessons/{lesson-id}")
    LessonDto getLesson(@PathVariable("lesson-id") UUID id);

    @GetMapping("/images/{image-id}")
    ImageDto getImageById(@PathVariable("image-id") UUID imageId);

    @GetMapping("/group-lessons/all")
    List<GroupLessonDto> getAllGroupLessons();

    @GetMapping("/group-lessons/{lesson-id}")
    GroupLessonDto getGroupLesson(@PathVariable(name = "lesson-id") UUID lessonId);

    @GetMapping("/timeslots/available-next-month-search")
    List<TimeSlotDto> getTeacherAvailableTimeSlotsInNextMonth(@RequestParam("teacher-id") UUID teacherId);

    @GetMapping("/timeslots/available-by-date")
    List<TimeSlotDto> getTeacherAvailableTimeSlotsByDate(@RequestParam("teacher-id") UUID teacherId, @RequestParam LocalDate date);

    @GetMapping("/timeslots/{timeslot-id}")
    TimeSlotDto getTimeSlot(@PathVariable("timeslot-id") UUID timeslotId);

    @GetMapping("/group-timeslots/available-next-month-search")
    List<GroupTimeSlotDto> getTeacherAvailableGroupTimeSlotInNextMonth(@RequestParam("teacher-id") UUID teacherId);

    @GetMapping("/group-timeslots/available-by-date")
    List<GroupTimeSlotDto> getAvailableGroupTimeSlotsByDate(@RequestParam("teacher-id") UUID teacherId,
                                                            @RequestParam("date") LocalDate date);

    @GetMapping("/group-timeslots/{group-timeslot-id}")
    GroupTimeSlotDto getGroupTimeSlot(@PathVariable("group-timeslot-id") UUID groupTimeslotId);

    @PostMapping("/registrations")
    RegistrationDto addRegistration(@RequestBody RegistrationDto registrationDto);

    @GetMapping("/registrations/{id}/created-at")
    CreateAtRegistrationDto getCreatedAt(@PathVariable("id") UUID id);

    @GetMapping("/registrations/actual")
    List<RegistrationDto> getAllUserActualRegistrations(@RequestParam("user-id") UUID userId);

    @GetMapping("/registrations/{id}")
    RegistrationDto getRegistration(@PathVariable("id") UUID id);

    @PostMapping("/group-registrations")
    GroupRegistrationDto addGroupRegistration(@RequestBody GroupRegistrationDto groupRegistrationDto);

    @PutMapping("/registrations/{registration-id}/confirm")
    RegistrationDto confirmRegistration(@PathVariable("registration-id") UUID registrationId);

    @PutMapping("/registrations/{registration-id}/decline")
    RegistrationDto declineRegistration(@PathVariable("registration-id") UUID registrationId);

    @PostMapping("/cancellations")
    CancelDto addCancel(@RequestBody CancelDto cancelDto);
}
