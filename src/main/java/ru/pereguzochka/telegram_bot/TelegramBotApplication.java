package ru.pereguzochka.telegram_bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;

@SpringBootApplication
public class TelegramBotApplication {
	@Autowired
	private TelegramBot bot;

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}

	@Bean
	public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
		TelegramBotsApi botApi = new TelegramBotsApi(DefaultBotSession.class);
		botApi.registerBot(bot);
		return botApi;
	}

}
