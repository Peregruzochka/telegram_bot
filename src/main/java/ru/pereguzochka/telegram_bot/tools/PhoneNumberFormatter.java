package ru.pereguzochka.telegram_bot.tools;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {
    private static final String PHONE_REGEX = "^(\\+7|8)?\\s*\\(?\\d{3}\\)?\\s*-?\\d{3}-?\\d{2}-?\\d{2}$";

    public String formatPhoneNumber(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        if (!phone.matches(PHONE_REGEX)) {
            throw new IllegalArgumentException("Phone number incorrect");
        }

        phone = phone.replaceAll("\\D", "");

        if (phone.length() == 11 && (phone.startsWith("7") || phone.startsWith("8"))) {
            phone = "+7" + phone.substring(1);
        } else if (phone.length() == 10) {
            phone = "+7" + phone;
        } else {
            throw new IllegalArgumentException("Phone number incorrect");
        }

        return phone.replaceFirst("(\\+7)(\\d{3})(\\d{3})(\\d{2})(\\d{2})", "$1 $2 $3-$4-$5");
    }
}
