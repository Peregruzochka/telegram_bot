package ru.pereguzochka.telegram_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChildDto implements Serializable {
    private UUID id;
    private String name;
    private String birthday;
    private ChildStatus status;

    public enum ChildStatus {
        NEW,
        REGULAR,
        EDITING,
    }
}
