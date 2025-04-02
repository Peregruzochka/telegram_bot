package ru.pereguzochka.telegram_bot.handler.cancel;

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
import java.util.ArrayList;
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
    private String userErrorChooseButtonText;

    public String generateText(RegistrationDto registrationDto, LocalDateTime createdAt) {
        if (forAutomaticCancel(registrationDto)) {
            return text;
        } else {
            if (isFiveMinutesAgo(createdAt)) {
                return text;
            } else {
                return manualCancelText;
            }
        }
    }

    public InlineKeyboardMarkup generateCancelCaseMarkup(RegistrationDto registrationDto, LocalDateTime createdAt) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>(createMarkup().getKeyboard());

        if (forAutomaticCancel(registrationDto)) {
            List<InlineKeyboardButton> illButton = List.of(
                    super.createButton(illButtonText, cancelCaseCallback + illButtonText)
            );
            List<InlineKeyboardButton> familyButton = List.of(
                    super.createButton(familyReasonsButtonText, cancelCaseCallback + familyReasonsButtonText)
            );

            buttons.add(0, familyButton);
            buttons.add(0, illButton);
        }

        if (LocalDateTime.now().minusMinutes(5).isBefore(createdAt)) {
            List<InlineKeyboardButton> userErrorButton = List.of(
                    super.createButton(userErrorChooseButtonText, cancelCaseCallback + userErrorChooseButtonText)
            );

            buttons.add(0, userErrorButton);
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();
    }

    private boolean isFiveMinutesAgo(LocalDateTime createdAt) {
        return LocalDateTime.now().minusMinutes(5).isBefore(createdAt);
    }

    private boolean forAutomaticCancel(RegistrationDto registrationDto) {
        TimeSlotDto timeSlotDto = registrationDto.getSlot();
        LocalDateTime startTime = timeSlotDto.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        return now.plusDays(1).isBefore(startTime);
    }
}
