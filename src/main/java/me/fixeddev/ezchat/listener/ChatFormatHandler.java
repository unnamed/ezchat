package me.fixeddev.ezchat.listener;

import org.bukkit.event.player.PlayerEvent;

import java.util.function.Consumer;

public interface ChatFormatHandler<E extends PlayerEvent> extends Consumer<E> {
    @Override
    void accept(E event);
}
