package ru.pereguzochka.telegram_bot.handler.cancel_registration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pereguzochka.telegram_bot.dto.RegistrationDto;
import ru.pereguzochka.telegram_bot.dto.TimeSlotDto;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "attr.choose-case")
public class CancelCaseAttribute extends BaseAttribute {
    private String manualCancelText;
    private String illButtonText;
    private String familyReasonsButtonText;
    private String cancelCaseCallback;

    public String generateText(RegistrationDto registrationDto) {
        return forAutomaticCancel(registrationDto) ? super.getText() : manualCancelText;
    }

    public InlineKeyboardMarkup generateCancelCaseMarkup(RegistrationDto registrationDto) {
        if (forAutomaticCancel(registrationDto)) {
            List<InlineKeyboardButton> illButton = List.of(
                    super.createButton(illButtonText, cancelCaseCallback + "ill")
            );
            List<InlineKeyboardButton> familyButton = List.of(
                    super.createButton(familyReasonsButtonText, cancelCaseCallback + "family")
            );
            return super.generateMarkup(List.of(illButton, familyButton));
        } else {
            return super.createMarkup();
        }
    }

    private boolean forAutomaticCancel(RegistrationDto registrationDto) {
        TimeSlotDto timeSlotDto = registrationDto.getSlot();
        LocalDateTime startTime = timeSlotDto.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        return now.plusDays(1).isBefore(startTime);
    }
}
