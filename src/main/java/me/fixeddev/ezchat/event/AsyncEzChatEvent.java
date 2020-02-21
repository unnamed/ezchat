package me.fixeddev.ezchat.event;

import me.fixeddev.ezchat.EasyTextComponent;
import me.fixeddev.ezchat.format.ChatFormat;
import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class AsyncEzChatEvent extends Event implements Cancellable {
    private final AsyncPlayerChatEvent event;

    private boolean cancelled;

    private BaseComponent format;
    private boolean isFormatFromChatFormat;
    private ChatFormat playerChatFormat;

    private static HandlerList handlerList = new HandlerList();

    public AsyncEzChatEvent(AsyncPlayerChatEvent event, ChatFormat chatFormat, BaseComponent format, boolean isFormatFromChatFormat) {
        super(event.isAsynchronous());
        this.event = event;

        this.playerChatFormat = chatFormat;
        this.format = format;
        this.isFormatFromChatFormat = isFormatFromChatFormat;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public BaseComponent getFormat() {
        return this.format;
    }

    public ChatFormat getPlayerChatFormat() {
        return playerChatFormat;
    }

    public void setFormat(String format){
        this.setFormat(format, playerChatFormat.isUsePlaceholderApi());
    }

    public void setFormat(String format, boolean usePlaceholderApi) {
        format = format.replace("{name}", getEvent().getPlayer().getName())
                .replace("{displayName}", getEvent().getPlayer().getDisplayName())
                .replace("{message}", getEvent().getMessage());

        format = ChatColor.translateAlternateColorCodes('&', format);

        if (usePlaceholderApi) {
            format = PlaceholderReplacer.getInstance().replacePlaceholders(getEvent().getPlayer(), format);
        }

        setFormat(EasyTextComponent.appendAll(TextComponent.fromLegacyText(format)));
    }

    public void setFormat(BaseComponent format) {
        if (!this.format.equals(format)) {
            isFormatFromChatFormat = false;
        }

        this.format = format;
    }

    public boolean isFormatFromChatFormat() {
        return isFormatFromChatFormat;
    }

    public AsyncPlayerChatEvent getEvent() {
        return event;
    }

    public String getMessage() {
        return event.getMessage();
    }

    public void setMessage(String message) {
        event.setMessage(message);
    }

    public Set<Player> getRecipients() {
        return event.getRecipients();
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
