package ru.pereguzochka.telegram_bot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ImageDto {
    private UUID id;
    private byte[] image;
    private String filename;
}
