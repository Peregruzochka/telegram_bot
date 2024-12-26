package ru.pereguzochka.telegram_bot.cache;

import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.ImageDto;

import java.util.UUID;

@Component
public class ImageCache extends Cache<UUID, ImageDto> {
}
