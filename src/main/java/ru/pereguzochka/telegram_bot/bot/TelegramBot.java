package ru.pereguzochka.telegram_bot.bot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pereguzochka.telegram_bot.cache.DeletedMessageCache;
import ru.pereguzochka.telegram_bot.cache.FileIDCache;
import ru.pereguzochka.telegram_bot.dto.ImageDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;



@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;

    private final List<UpdateHandler> handlers;
    private final FileIDCache fileIDCache;

    public TelegramBot(@Lazy List<UpdateHandler> handlers,
                       FileIDCache fileIDCache,
                       DeletedMessageCache deletedMessageCache) {
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

    public void sendImage(String imageUrl, Update update) {
        Long chatId = -1L;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }

        File imageFile = new File(imageUrl);
        InputFile inputFile = new InputFile(imageFile);

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(inputFile)
                .build();

        try {
            execute(sendPhoto).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Integer> sendImages(List<ImageDto> images, Update update) {
        List<InputMedia> mediaPhotos = images.stream()
                .map(this::createInputMedia)
                .toList();

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendMediaGroup mediaGroup = SendMediaGroup.builder()
                .chatId(chatId)
                .medias(mediaPhotos)
                .build();

        try {
            List<Message> messages = this.execute(mediaGroup);

            for (int i = 0; i < images.size(); i++) {
                UUID imageId = images.get(i).getId();
                String telegramFileId = messages.get(i).getPhoto().get(0).getFileId();
                fileIDCache.put(imageId, telegramFileId);
            }

            return messages.stream()
                    .map(Message::getMessageId)
                    .toList();

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InputMedia createInputMedia(ImageDto imageDto) {
        if (!fileIDCache.contains(imageDto.getId())) {
            byte[] imageBytes = imageDto.getImage();
            InputStream imageStream = new ByteArrayInputStream(imageBytes);
            return InputMediaPhoto.builder()
                    .media("attach://" + imageDto.getFilename())
                    .isNewMedia(true)
                    .mediaName(imageDto.getFilename())
                    .newMediaStream(imageStream)
                    .build();
        } else {
            String telegramFileId = fileIDCache.get(imageDto.getId());
            return InputMediaPhoto.builder()
                    .media(telegramFileId)
                    .isNewMedia(false)
                    .build();
        }
    }
}


