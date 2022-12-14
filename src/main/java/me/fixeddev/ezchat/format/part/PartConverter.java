package me.fixeddev.ezchat.format.part;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.function.Function;

public interface PartConverter<T extends ChatPart<T>> {
    Component convert(T part, Player player);

    Component convert(T part, Player player, Audience viewer);

    String componentToString(Component component);

    default Function<T, Component> functionForPlayer(Player player) {
        return t -> convert(t, player);
    }

    default Function<ChatPart<?>, Component> unsafeFunctionForPlayer(Player player) {
        return t -> convert((T) t, player);
    }

    default Function<T, Component> functionForPlayer(Player player, Audience viewer) {
        return t -> convert(t, player, viewer);
    }

    default Function<ChatPart<?>, Component> unsafeFunctionForPlayer(Player player, Audience viewer) {
        return t -> convert((T) t, player, viewer);
    }

}
