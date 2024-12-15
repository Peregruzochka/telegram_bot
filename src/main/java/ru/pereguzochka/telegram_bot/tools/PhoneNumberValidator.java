package ru.pereguzochka.telegram_bot.tools;

import org.springframework.stereotype.Component;
import static java.util.regex.Pattern.matches;

@Component
public class PhoneNumberValidator {
    private static final String PHONE_REGEX = "^(\\+7|8)?\\s*\\(?\\d{3}\\)?\\s*-?\\d{3}-?\\d{2}-?\\d{2}$";

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return matches(PHONE_REGEX, phoneNumber);
    }
}
