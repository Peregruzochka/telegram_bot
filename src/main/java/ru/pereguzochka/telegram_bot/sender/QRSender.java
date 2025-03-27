package ru.pereguzochka.telegram_bot.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;
import ru.pereguzochka.telegram_bot.handler.MainMenuPortAttribute;


@Component
@RequiredArgsConstructor
public class QRSender {
    private final QRSenderAttribute qrSenderAttribute;
    private final TelegramBot telegramBot;
    private final MainMenuPortAttribute mainMenuPortAttribute;

    @Value("${qr-code-url}")
    private String qrCodeUrl;

    public void send(RegistrationEvent event) {

        telegramBot.sendImage(
                qrCodeUrl,
                qrSenderAttribute.generateText(event),
                event.getTelegramId()
        );

        telegramBot.send(
                mainMenuPortAttribute.getText(),
                mainMenuPortAttribute.createMarkup(),
                event.getTelegramId()
        );
    }
}
