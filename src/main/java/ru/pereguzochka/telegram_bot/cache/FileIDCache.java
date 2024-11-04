package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class FileIDCache {
    Map<String, String> cache = new HashMap<>();
}
