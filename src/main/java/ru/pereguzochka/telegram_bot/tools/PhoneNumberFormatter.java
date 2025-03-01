package ru.pereguzochka.telegram_bot.tools;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {
    public String formatPhoneNumber(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        // Убираем все нецифровые символы
        phone = phone.replaceAll("\\D", "");

        // Проверяем, что номер содержит 11 цифр и начинается с 8 или 7
        if (phone.length() == 11) {
            if (phone.startsWith("8")) {
                return "+7" + phone.substring(1);
            } else if (phone.startsWith("7")) {
                return "+" + phone;
            }
        }

        throw new IllegalArgumentException("Phone number incorrect"); // Некорректный номер
    }
}
