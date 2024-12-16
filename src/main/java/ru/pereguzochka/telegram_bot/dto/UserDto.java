package ru.pereguzochka.telegram_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID id;
    private Long telegramId;
    private String username;
    private String phone;
    private List<ChildrenDto> children;
}
