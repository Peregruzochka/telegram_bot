package ru.pereguzochka.telegram_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pereguzochka.telegram_bot.cache.FileIDCache;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.io.File;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    private final List<UpdateHandler> handlers;
    private final FileIDCache fileIDCache;

    public TelegramBot(@Lazy List<UpdateHandler> handlers, FileIDCache fileIDCache) {
        this.handlers = handlers;
        this.fileIDCache = fileIDCache;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info(update.toString());

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

    public void send(String text, ReplyKeyboard markup, Update update) {
        Long chatId = -1L;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if(update.hasCallbackQuery()) {
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
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
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

    public void sendPhotos(List<String> paths, Update update) {
        List<InputMedia> mediaPhotos = paths.stream()
                .map(this::createInputMedia)
                .toList();

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendMediaGroup mediaGroup = SendMediaGroup.builder()
                .chatId(chatId)
                .medias(mediaPhotos)
                .build();

        try {
            List<Message> messages = this.execute(mediaGroup);
            Map<String, String> fileIdCache = fileIDCache.getCache();
            for (int i = 0; i < paths.size(); i++) {
                String path = paths.get(i);
                String fileId = messages.get(i).getPhoto().get(0).getFileId();
                fileIdCache.put(path, fileId);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InputMedia createInputMedia(String path) {
        String fileId = fileIDCache.getCache().get(path);
        if (fileId != null) {
            return new InputMediaPhoto(fileId);
        } else {
            File file = new File(path);
            return InputMediaPhoto.builder()
                    .media("attach://" + file.getName())
                    .isNewMedia(true)
                    .mediaName(file.getName())
                    .newMediaFile(file)
                    .build();
        }
    }
}


