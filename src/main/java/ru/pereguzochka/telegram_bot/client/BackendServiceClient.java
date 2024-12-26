package ru.pereguzochka.telegram_bot.client;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
public class BackendServiceClient {

    public UserDto getUserByTelegramId(Long telegramId) {
        //TODO: настроить взаимодействие с backend
        return null;
    }

    public List<LessonDto> getAllLessons() {
        //TODO: настроить взаимодействие
        return generateLessons();
    }

    public ImageDto getImageById(UUID imageId) {
        //TODO: настроить взаимодействие
        return getTestImageById(imageId);
    }

    public List<TimeSlotDto> getMonthFreeTeacherTimeSlots(UUID teacherId) {
        //TODO: настроить взаимодействие
        return generateTestTimeSlots();
    }

    private List<LessonDto> generateLessons() {
        List<LessonDto> lessonDtoList = new ArrayList<>();

        lessonDtoList.add(LessonDto.builder()
                .id(UUID.randomUUID())
                .name("Логопед")
                .description("Поможем детям с развитием речи")
                .teachers(List.of(
                        TeacherDto.builder()
                                .id(UUID.randomUUID())
                                .name("Татьяна")
                                .imageID(UUID.fromString("90002bbc-5c0e-4a2c-95af-b1ef3c24ab62"))
                                .build(),
                        TeacherDto.builder()
                                .id(UUID.randomUUID())
                                .name("Ирина")
                                .imageID(UUID.fromString("e958c6ed-6165-4253-a064-7e90af174712"))
                                .build()
                ))
                .build());

        return lessonDtoList;
    }

    private ImageDto getTestImageById(UUID imageId) {
        if (imageId.equals(UUID.fromString("90002bbc-5c0e-4a2c-95af-b1ef3c24ab62"))) {
            File file = new File("src/main/resources/photo/teacher/teacher1.jpg");
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] image = inputStream.readAllBytes();
                return ImageDto.builder()
                        .id(imageId)
                        .filename(file.getName())
                        .image(image)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (imageId.equals(UUID.fromString("e958c6ed-6165-4253-a064-7e90af174712"))) {
            File file = new File("src/main/resources/photo/teacher/teacher2.jpg");
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] image = inputStream.readAllBytes();
                return ImageDto.builder()
                        .id(imageId)
                        .filename(file.getName())
                        .image(image)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private List<TimeSlotDto> generateTestTimeSlots() {
        List<TimeSlotDto> timeSlots = new ArrayList<>();
        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1); // Начать с завтрашнего дня

        for (int i = 0; i < 7; i++) {
            LocalDateTime currentDay = startOfDay.plusDays(i);
            int numberOfSlots = 2 + (int) (Math.random() * 2); // Генерируем 2 или 3 занятия

            for (int j = 0; j < numberOfSlots; j++) {
                LocalDateTime startTime = currentDay.plusHours(9 + j); // Начинаем с 9 утра
                LocalDateTime endTime = startTime.plusMinutes(45); // Длительность занятия 45 минут

                TimeSlotDto timeSlot = TimeSlotDto.builder()
                        .id(UUID.randomUUID())
                        .startTime(startTime)
                        .endTime(endTime)
                        .teacher(null) // Пример без указания преподавателя, можно заменить на конкретного
                        .build();

                timeSlots.add(timeSlot);
            }
        }
        return timeSlots;
    }
}



