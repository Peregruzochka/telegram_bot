package ru.pereguzochka.telegram_bot.handler.input_handler.check_data;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.check-registration-data")
@RequiredArgsConstructor
public class CheckDataAttribute extends BaseAttribute {
    public String generateText(RegistrationDto registrationDto) {
        return super.text.replace("{}", createUserInfo(registrationDto));
    }

    public String createUserInfo(RegistrationDto registrationDto) {
        return "<b>Ваше имя: </b>" + registrationDto.getUser().getName() + "\n" +
                "<b>Ваш номер телефона: </b>" + registrationDto.getUser().getPhone() + "\n" +
                "<b>Ваш ребенок: </b>" + registrationDto.getChild().getName() +
                " <i>(" + registrationDto.getChild().getBirthday() + ")</i>" + "\n";
    }
}
