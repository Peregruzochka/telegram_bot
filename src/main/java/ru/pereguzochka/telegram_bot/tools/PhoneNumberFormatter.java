package ru.pereguzochka.telegram_bot.tools;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {
    private static final String PHONE_REGEX = "^(\\+7|8)?\\s*\\(?\\d{3}\\)?\\s*-?\\d{3}-?\\d{2}-?\\d{2}$";

    public String formatPhoneNumber(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        // Проверяем, что номер соответствует валидному формату
        if (!phone.matches(PHONE_REGEX)) {
            throw new IllegalArgumentException("Phone number incorrect");
        }

        // Убираем все нецифровые символы
        phone = phone.replaceAll("\\D", "");

        // Обрабатываем номер с учетом кода страны
        if (phone.length() == 11 && (phone.startsWith("7") || phone.startsWith("8"))) {
            return "+7" + phone.substring(1);
        } else if (phone.length() == 10) {
            return "+7" + phone;
        }

        throw new IllegalArgumentException("Phone number incorrect");
    }
}
