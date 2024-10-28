package ru.pereguzochka.telegram_bot.handler.direction_handler.info.diagnostic_psycholog_neuropsycholog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.diagnostic-psycholog-neuropsycholog")
public class DiagnosticPsychologistNeuropsychologistAttribute extends BaseAttribute {

}
