package ru.pereguzochka.telegram_bot.cache;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeletedMessageCache extends Cache<Long, List<Integer>> {
//    Key:      ChatId
//    Value:    List<MessageId>
}
