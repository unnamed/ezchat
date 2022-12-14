package me.fixeddev.ezchat.format.part;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.function.Function;

public interface ChatPart<T extends ChatPart<T>> extends ConfigurationSerializable {
    default String toChatString(Function<T, Component> functionConverter, PartConverter<T> partConverter) {
        return partConverter.componentToString(toComponent(functionConverter));
    }

    Component toComponent(Function<T, Component> functionConverter);

    T copy();
}
