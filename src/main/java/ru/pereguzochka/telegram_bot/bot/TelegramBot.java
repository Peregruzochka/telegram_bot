package ru.pereguzochka.telegram_bot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    private final List<UpdateHandler> handlers;

    public TelegramBot(@Lazy List<UpdateHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info(update.getMessage().toString());

        UpdateHandler handler = handlers.stream()
                .filter(updateHandler -> updateHandler.isApplicable(update))
                .findFirst()
                .orElseThrow();

        handler.compute(update);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void send(String text, ReplyKeyboard markup, Long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .text(text)
                .replyMarkup(markup)
                .chatId(chatId)
                .parseMode("HTML")
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}


