package ru.pereguzochka.telegram_bot.handler.teacher_handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.TeacherDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "attr.teachers")
public class TeacherAttribute extends BaseAttribute {
    private List<UUID> uuids;
    private List<String> paths;
    @JsonProperty("teacher-callbacks")
    private List<String> teacherCallbacks;

    public List<String> createPaths(List<TeacherDto> teachers) {
        List<String> truePaths = new ArrayList<>();
        for (TeacherDto teacher : teachers) {
            int index = uuids.indexOf(teacher.getId());
            truePaths.add(paths.get(index));
        }
        return truePaths;
    }

    public UUID getIdByCallback(String callback) {
        int index = teacherCallbacks.indexOf(callback);
        return uuids.get(index);
    }

    public InlineKeyboardMarkup createMarkup(List<TeacherDto> teachers) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (TeacherDto teacher : teachers) {
            int index = uuids.indexOf(teacher.getId());
            String teacherCallback = teacherCallbacks.get(index);
            InlineKeyboardButton button = super.createButton(teacher.getName(), teacherCallback);
            keyboard.add(List.of(button));
        }

        InlineKeyboardMarkup oldMarkup = super.createMarkup();
        List<List<InlineKeyboardButton>> oldButtons = oldMarkup.getKeyboard();

        keyboard.addAll(oldButtons);

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}

