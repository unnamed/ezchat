package me.fixeddev.ezchat.listener;

import me.fixeddev.ezchat.EasyTextComponent;
import me.fixeddev.ezchat.event.AsyncEzChatEvent;
import me.fixeddev.ezchat.format.ChatFormat;
import me.fixeddev.ezchat.format.ChatFormatManager;
import me.fixeddev.ezchat.format.ChatFormatSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class AbstractChatListener implements Listener {

    private ChatFormatManager chatFormatManager;
    private ChatFormatSerializer chatFormatSerializer;

    public AbstractChatListener(ChatFormatManager chatFormatManager) {
        this.chatFormatManager = chatFormatManager;

        this.chatFormatSerializer = new ChatFormatSerializer();
    }

    public void formatChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        Bukkit.getConsoleSender().sendMessage(String.format(event.getFormat(), player.getName(), event.getMessage()));

        ChatFormat chatFormat = chatFormatManager.getChatFormatForPlayer(player);
        BaseComponent chatFormatComponent;

        String message = event.getMessage();

        if (player.hasPermission("ezchat.color")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        message = ChatColor.translateAlternateColorCodes('&', chatFormat.getChatColor()) + message;

        BaseComponent messageComponent = EasyTextComponent.appendAll(TextComponent.fromLegacyText(message));

        boolean eventAlreadyCalled = false;

        if (chatFormat.isUsePlaceholderApi()) {
            chatFormatComponent = chatFormatSerializer.constructJsonMessage(chatFormat, player);

            if (chatFormat.isAllowRelationalPlaceholders()) {
                AsyncEzChatEvent chatEvent = new AsyncEzChatEvent(event, chatFormat, chatFormatComponent, true);

                Bukkit.getPluginManager().callEvent(chatEvent);

                eventAlreadyCalled = true;

                if (chatEvent.isCancelled()) {
                    return;
                }

                // Only create and send the format in this way if the format is still the same
                // Otherwise, proceed as a normal format
                if (chatEvent.isFormatFromChatFormat()) {
                    for (Player recipient : event.getRecipients()) {
                        chatFormatComponent = chatFormatSerializer.constructJsonMessage(chatFormat, player, recipient);
                        chatFormatComponent.addExtra(messageComponent);

                        recipient.spigot().sendMessage(chatFormatComponent);
                    }

                    return;
                }

                chatFormatComponent = chatEvent.getFormat();
            }
        } else {
            chatFormatComponent = chatFormatSerializer.constructJsonMessageWithoutPlaceholders(chatFormat, player);
        }

        if (!eventAlreadyCalled) {
            AsyncEzChatEvent chatEvent = new AsyncEzChatEvent(event, chatFormat, chatFormatComponent, true);

            Bukkit.getPluginManager().callEvent(chatEvent);

            if (chatEvent.isCancelled()) {
                return;
            }

            chatFormatComponent = chatEvent.getFormat();
        }

        chatFormatComponent.addExtra(messageComponent);

        for (Player recipient : event.getRecipients()) {
            recipient.spigot().sendMessage(chatFormatComponent);
        }
    }
}
