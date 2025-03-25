package ru.pereguzochka.telegram_bot;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;

@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients(basePackages = "ru.pereguzochka.telegram_bot.client")
public class TelegramBotApplication {
	private final TelegramBot bot;


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
