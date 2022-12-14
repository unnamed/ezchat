package me.fixeddev.ezchat.event;

import me.fixeddev.ezchat.format.ChatFormat;
import me.fixeddev.ezchat.format.NewChatFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;

import java.util.Set;

public class AsyncEzChatEvent extends Event implements Cancellable {
    private final PlayerEvent event;
    private final NewChatFormat playerChatFormat;

    private boolean cancelled;
    private static final HandlerList handlerList = new HandlerList();

    public AsyncEzChatEvent(PlayerEvent event, NewChatFormat chatFormat) {
        super(event.isAsynchronous());
        this.event = event;

        this.playerChatFormat = chatFormat;
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

    public PlayerEvent getEvent() {
        return event;
    }

    public Player getPlayer() {
        return event.getPlayer();
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

}
