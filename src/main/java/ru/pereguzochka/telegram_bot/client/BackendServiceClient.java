package ru.pereguzochka.telegram_bot.client;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.ChildDto;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.dto.UserDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Component
public class BackendServiceClient {

    public UserDto getUserByTelegramId(Long telegramId) {
        //TODO: настроить взаимодействие с backend
        return generateTestUser(telegramId);
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

    public List<RegistrationDto> getAllUserRegistrations(Long telegramId) {
        //TODO: настроить взаимодействие
        return generateTestRegistrations(telegramId);
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

    private UserDto generateTestUser(Long telegramId) {
        return UserDto.builder()
                .id(UUID.randomUUID()) // Генерация случайного UUID
                .telegramId(telegramId) // Устанавливаем переданный telegramId
                .name("Иван Иванов") // Русское имя
                .phone("+79876543210") // Пример российского номера телефона
                .children(Arrays.asList(
                        ChildDto.builder()
                                .id(UUID.randomUUID())
                                .name("Маша") // Русское имя ребенка
                                .birthday("2017-06-15") // Дата рождения в формате "yyyy-MM-dd"
                                .build(),
                        ChildDto.builder()
                                .id(UUID.randomUUID())
                                .name("Кирилл") // Русское имя ребенка
                                .birthday("2019-10-22") // Дата рождения в формате "yyyy-MM-dd"
                                .build()
                )) // Добавляем пример списка детей
                .build();
    }

    private List<RegistrationDto> generateTestRegistrations(Long telegramId) {
        List<RegistrationDto> registrations = new ArrayList<>();
        ChildDto child1 = ChildDto.builder()
                .id(UUID.randomUUID())
                .name("Иван Иванов")
                .birthday("2010-05-12")
                .build();

        ChildDto child2 = ChildDto.builder()
                .id(UUID.randomUUID())
                .name("Мария Иванова")
                .birthday("2012-08-24")
                .build();

        // Создание пользователя
        UserDto user = UserDto.builder()
                .id(UUID.randomUUID())
                .telegramId(telegramId)
                .name("Петр Смирнов")
                .phone("+7 900 123-45-67")
                .children(List.of(child1, child2))
                .build();

        // Создание преподавателя
        TeacherDto teacher = TeacherDto.builder()
                .id(UUID.randomUUID())
                .name("Елена Петровна")
                .imageID(UUID.randomUUID())
                .build();

        // Создание уроков
        LessonDto lesson1 = LessonDto.builder()
                .id(UUID.randomUUID())
                .name("Математика")
                .description("Основы алгебры")
                .teachers(List.of(teacher))
                .build();

        LessonDto lesson2 = LessonDto.builder()
                .id(UUID.randomUUID())
                .name("Физика")
                .description("Механика и силы")
                .teachers(List.of(teacher))
                .build();

        // Создание временных слотов
        TimeSlotDto slot1 = TimeSlotDto.builder()
                .id(UUID.randomUUID())
                .startTime(LocalDateTime.of(2024, 12, 27, 10, 0))
                .endTime(LocalDateTime.of(2024, 12, 27, 11, 30))
                .teacher(teacher)
                .build();

        TimeSlotDto slot2 = TimeSlotDto.builder()
                .id(UUID.randomUUID())
                .startTime(LocalDateTime.of(2024, 12, 27, 12, 0))
                .endTime(LocalDateTime.of(2024, 12, 27, 13, 30))
                .teacher(teacher)
                .build();

        // Создание RegistrationDto
        RegistrationDto registration1 = RegistrationDto.builder()
                .id(UUID.randomUUID())
                .telegramId(user.getTelegramId())
                .user(user)
                .child(child1)
                .lesson(lesson1)
                .teacher(teacher)
                .type(RegistrationDto.RegistrationType.NEW_USER)
                .slot(slot1)
                .build();

        RegistrationDto registration2 = RegistrationDto.builder()
                .id(UUID.randomUUID())
                .telegramId(user.getTelegramId())
                .user(user)
                .child(child2)
                .lesson(lesson2)
                .teacher(teacher)
                .type(RegistrationDto.RegistrationType.REGULAR_USER)
                .slot(slot2)
                .build();
        registrations.add(registration1);
        registrations.add(registration2);
        return registrations;
    }

    public void postRegistration(RegistrationDto registrationDto) {
        //TODO настроить взаимодействие
        //TODO количество попыток 3
    }

    public void cancelRegistration(RegistrationDto registrationDto, String caseDescription) {
        //TODO настроить взаимодейсвие
    }
}


