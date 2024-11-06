package ru.pereguzochka.telegram_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegistrationDto {
    private Long telegramId;
    private String username;
    private UUID lessonId;
    private UUID teacherId;
    private RegistrationType type;
    private UUID slotId;
    private String childrenName;
    private int childrenYear;
    private int childrenMonth;
    private String phone;

    public enum RegistrationType {
        NEW_USER,
        REGULAR_USER,
        RE_REGISTRATION
    }
}
