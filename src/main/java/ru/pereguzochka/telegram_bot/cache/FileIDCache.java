package ru.pereguzochka.telegram_bot.cache;

import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class FileIDCache extends Cache<UUID, String> {
    //key -> imageId
    //value -> fileId in tg
}
