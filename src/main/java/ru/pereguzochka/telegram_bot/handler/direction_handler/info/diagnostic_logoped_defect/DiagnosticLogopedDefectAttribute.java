package ru.pereguzochka.telegram_bot.handler.direction_handler.info.diagnostic_logoped_defect;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.diagnostic-logoped-defect")
public class DiagnosticLogopedDefectAttribute extends BaseAttribute {
}
