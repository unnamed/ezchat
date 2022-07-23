package me.fixeddev.ezchat.listener;

import me.fixeddev.ezchat.EasyTextComponent;
import me.fixeddev.ezchat.event.AsyncEzChatEvent;
import me.fixeddev.ezchat.format.ChatFormat;
import me.fixeddev.ezchat.format.ChatFormatManager;
import me.fixeddev.ezchat.format.ChatFormatSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractChatListener implements Listener {

    private final ChatFormatManager chatFormatManager;
    private final ChatFormatSerializer chatFormatSerializer;

    private final boolean alternativeChatHandling;

    public AbstractChatListener(ChatFormatManager chatFormatManager, boolean alternativeChatHandling) {
        this.chatFormatManager = chatFormatManager;

        this.chatFormatSerializer = new ChatFormatSerializer();
        this.alternativeChatHandling = alternativeChatHandling;
    }

    public void formatChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Set<Player> recipients = event.getRecipients();

        if (alternativeChatHandling) {
            event.setCancelled(true);
        } else {
            recipients = new HashSet<>(event.getRecipients());
            event.getRecipients().clear();
        }


        ChatFormat chatFormat = chatFormatManager.getChatFormatForPlayer(player).copy();

        String message = ChatFormatSerializer.replacePlaceholders(player, chatFormat.getChatColor()) + event.getMessage();

        if (player.hasPermission("ezchat.color")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        BaseComponent[] messageComponent = TextComponent.fromLegacyText(message);

        AsyncEzChatEvent chatEvent = new AsyncEzChatEvent(event, chatFormat, recipients);

        Bukkit.getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        Bukkit.getConsoleSender().sendMessage(String.format(event.getFormat(), player.getName(), event.getMessage()));

        EasyTextComponent chatFormatComponent = null;

        if (!chatFormat.isUsePlaceholderApi()) {
            chatFormatComponent = chatFormatSerializer.constructJsonMessage(chatFormat, player);
            chatFormatComponent.append(messageComponent);
        }

        for (Player recipient : recipients) {
            if (chatFormat.isUsePlaceholderApi()) {
                chatFormatComponent = chatFormatSerializer.constructJsonMessage(chatFormat, player, recipient);

                chatFormatComponent.append(messageComponent);
            }


            recipient.spigot().sendMessage(chatFormatComponent.build());
        }
    }
}
