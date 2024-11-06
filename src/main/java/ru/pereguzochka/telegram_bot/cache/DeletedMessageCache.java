package ru.pereguzochka.telegram_bot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
public class DeletedMessageCache {
//    Key:      ChatId
//    Value:    List<MessageId>
    private final Map<Long, List<Integer>> cache = new HashMap<>();
}
