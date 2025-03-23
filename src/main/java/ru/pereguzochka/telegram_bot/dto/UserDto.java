package ru.pereguzochka.telegram_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDto implements Serializable {
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


