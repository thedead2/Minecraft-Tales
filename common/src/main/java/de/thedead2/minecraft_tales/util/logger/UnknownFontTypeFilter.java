package de.thedead2.minecraft_tales.util.logger;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.Objects;


@Plugin(name = "UnknownFontTypeFilter", category = Node.CATEGORY, elementType = Filter.ELEMENT_TYPE)
public class UnknownFontTypeFilter extends AbstractFilter {
    @Override
    public Result filter(LogEvent event) {
        Message message = event.getMessage();
        Throwable throwable = event.getThrown();
        if(Objects.equals(message.getFormat(), "Unable to load font '{}' in {} in resourcepack: '{}'") && throwable != null && throwable.getMessage().startsWith("Invalid type: ")) {
            return Result.DENY;
        }
        else {
            return Result.NEUTRAL;
        }
    }
}
