package ru.pereguzochka.telegram_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.FileIdCache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    private final List<UpdateHandler> handlers;
    private final FileIdCache fileIdCache;

    public TelegramBot(@Lazy List<UpdateHandler> handlers, @Lazy FileIdCache fileIdCache) {
        this.handlers = handlers;
        this.fileIdCache = fileIdCache;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug(update.toString());

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

    public Long extractTelegramId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        throw new IllegalArgumentException("Update has no message or callback");
    }

    public void send(String text, Update update) {
        Long chatId = -1L;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }

        SendMessage sendMessage = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .parseMode("HTML")
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String text, ReplyKeyboard markup, Update update) {
        Long chatId = -1L;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }

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

    public void send(String text, InlineKeyboardMarkup markup, Long chatId) {
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

    public void edit(String text, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String callbackId = update.getCallbackQuery().getId();

        EditMessageText newText = EditMessageText.builder()
                .chatId(chatId)
                .text(text)
                .messageId(messageId)
                .parseMode("HTML")
                .build();

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .build();

        try {
            this.execute(newText);
            this.execute(close);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void answer(Update update) {
        String callbackId = update.getCallbackQuery().getId();
        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .build();
        try {
            this.execute(close);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void edit(String text, InlineKeyboardMarkup markup, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String callbackId = update.getCallbackQuery().getId();


        EditMessageText newText = EditMessageText.builder()
                .chatId(chatId)
                .text(text)
                .messageId(messageId)
                .parseMode("HTML")
                .build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(markup)
                .build();

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .build();

        try {
            this.execute(newText);
            this.execute(newKb);
            this.execute(close);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Update update) {
        Long chatId = -1L;
        Integer messageId = -1;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            messageId = update.getMessage().getMessageId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        }

        delete(messageId, chatId);
    }

    public void delete(Integer messageId, Long chatId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .messageId(messageId)
                .chatId(chatId)
                .build();

        try {
            this.execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }

    public void sendImage(String imageUrl, String text, Update update) {
        Long chatId = -1L;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        sendImage(imageUrl, text, chatId);
    }

    public void sendImage(String imageUrl, String text, Long chatId) {
        File imageFile = new File(imageUrl);
        InputFile inputFile = new InputFile(imageFile);

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(inputFile)
                .caption(text)
                .parseMode("HTML")
                .build();

        try {
            execute(sendPhoto).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendImage(ImageDto imageDto, String text, InlineKeyboardMarkup markup, Update update) {
        InputFile inputFile = createInputFile(imageDto);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(inputFile)
                .caption(text)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();

        try {
            Message message = execute(sendPhoto);
            String imageUuid = imageDto.getId().toString();
            String fileId = message.getPhoto().get(0).getFileId();
            fileIdCache.put(imageUuid, fileId);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InputFile createInputFile(ImageDto imageDto) {
        String imageUuid = imageDto.getId().toString();
        if (!fileIdCache.exists(imageUuid)) {
            byte[] imageBytes = imageDto.getImage();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            return new InputFile(inputStream, imageDto.getFilename());
        } else {
            String telegramFileId = fileIdCache.get(imageUuid, String.class).orElseThrow();
            return new InputFile(telegramFileId);
        }
    }
}



