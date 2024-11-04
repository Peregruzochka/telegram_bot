package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeacherHandler implements UpdateHandler {

    private final TeacherAttribute attribute;
    private final TelegramBot bot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/teachers");
    }

    @Override
    public void compute(Update update) {
        List<TeacherDto> teachers = getTeachers();
        bot.sendPhotos(attribute.createPaths(teachers), update);
        bot.send(attribute.getText(), attribute.createMarkup(teachers), update);
        bot.delete(update);
    }

    private List<TeacherDto> getTeachers() {
        //TODO запрос в бэкэнд на основе registrationDto
        return new ArrayList<>() {{
            add(TeacherDto.builder()
                    .id(UUID.fromString("8ea4bc97-48fc-4787-943e-36ac42baa060"))
                    .name("Татьяна")
                    .build());
            add(TeacherDto.builder()
                    .id(UUID.fromString("fa8ed56d-a8da-431b-986c-eab37130b5eb"))
                    .name("Ирина")
                    .build());
            add(TeacherDto.builder()
                    .id(UUID.fromString("6e0fe30e-be0a-4ad4-a84e-1ee627e2740c"))
                    .name("Олеся")
                    .build());
        }};
    }
}

