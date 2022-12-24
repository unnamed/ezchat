package me.fixeddev.ezchat.listener;

import me.fixeddev.ezchat.event.AsyncEzChatEvent;
import me.fixeddev.ezchat.format.ChatFormatManager;
import me.fixeddev.ezchat.format.ChatFormatSerializer;
import me.fixeddev.ezchat.format.NewChatFormat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class OldChatFormatHandler implements ChatFormatHandler<AsyncPlayerChatEvent> {

    private final ChatFormatManager chatFormatManager;
    private final ChatFormatSerializer chatFormatSerializer;

    private final boolean alternativeChatHandling;

    private final BukkitAudiences bukkitAudiences;
    private final LegacyComponentSerializer componentSerializer;

    public OldChatFormatHandler(ChatFormatManager chatFormatManager, boolean alternativeChatHandling, JavaPlugin plugin) {
        this.chatFormatManager = chatFormatManager;

        this.chatFormatSerializer = new ChatFormatSerializer();
        this.alternativeChatHandling = alternativeChatHandling;

        bukkitAudiences = BukkitAudiences.create(plugin);
        componentSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().extractUrls().build();
    }

    @Override
    public void accept(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Set<Player> recipients = event.getRecipients();

        if (alternativeChatHandling) {
            event.setCancelled(true);
        } else {
            recipients = new HashSet<>(event.getRecipients());
            event.getRecipients().clear();
        }

        NewChatFormat chatFormat = chatFormatManager.getChatFormatForPlayer(player).copy();

        String message = ChatFormatSerializer.replacePlaceholders(player, chatFormat.getChatColor()) + event.getMessage();

        if (player.hasPermission("ezchat.color")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        Component messageComponent = componentSerializer.deserialize(message);

        AsyncEzChatEvent chatEvent = new AsyncEzChatEvent(event, chatFormat, messageComponent);

        Bukkit.getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        messageComponent = chatEvent.getMessage();

        Bukkit.getConsoleSender().sendMessage(String.format(event.getFormat(), player.getName(), event.getMessage()));

        Component chatFormatComponent = null;

        if (!chatFormat.usingPlaceholderApi()) {
            chatFormatComponent = chatFormatSerializer.constructJsonMessage(chatFormat, player).append(messageComponent);
        }

        for (Player recipient : recipients) {
            Audience recipientAudience = bukkitAudiences.player(recipient);
            if (chatFormat.usingPlaceholderApi()) {
                chatFormatComponent = chatFormatSerializer.constructJsonMessage(chatFormat, player, recipientAudience).append(messageComponent);
            }

            recipientAudience.sendMessage(chatFormatComponent);
        }
    }
}
