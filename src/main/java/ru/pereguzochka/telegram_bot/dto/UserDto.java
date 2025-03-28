package ru.pereguzochka.telegram_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private Long telegramId;
    private String name;
    private String phone;
    private UserStatus status;
    private List<ChildDto> children;

    public enum UserStatus {
        NEW,
        REGULAR,
        EDITING,
    }
}
