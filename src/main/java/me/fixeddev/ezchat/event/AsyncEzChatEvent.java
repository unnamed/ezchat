package me.fixeddev.ezchat.event;

import me.fixeddev.ezchat.format.NewChatFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AsyncEzChatEvent extends Event implements Cancellable {
    private final PlayerEvent event;
    private final NewChatFormat playerChatFormat;
    private Component message;
    private final Player player;

    private boolean cancelled;
    private static final HandlerList handlerList = new HandlerList();

    public AsyncEzChatEvent(PlayerEvent event, NewChatFormat chatFormat, Component message) {
        super(event.isAsynchronous());
        this.event = event;
        this.player = event.getPlayer();
        this.playerChatFormat = chatFormat;
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public NewChatFormat getPlayerChatFormat() {
        return playerChatFormat;
    }

    public Component getMessage() {
        return message;
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    public PlayerEvent getEvent() {
        return event;
    }

    /**
     * Returns the player involved in this event
     *
     * @return Player who is involved in this event
     */
    @NotNull
    public final Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

}
