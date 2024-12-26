package ru.pereguzochka.telegram_bot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ImageDto {
    private UUID id;
    private byte[] image;
    private String filename;
}
