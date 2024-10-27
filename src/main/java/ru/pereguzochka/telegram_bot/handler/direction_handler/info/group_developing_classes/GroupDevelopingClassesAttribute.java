package ru.pereguzochka.telegram_bot.handler.direction_handler.info.group_developing_classes;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.handler.BaseAttribute;

@Component
@ConfigurationProperties(prefix = "attr.group-developing-classes")
public class GroupDevelopingClassesAttribute extends BaseAttribute{
}
