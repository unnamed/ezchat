package me.fixeddev.ezchat.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AbstractChatEvent;
import me.fixeddev.ezchat.event.AsyncEzChatEvent;
import me.fixeddev.ezchat.format.ChatFormatManager;
import me.fixeddev.ezchat.format.ChatFormatSerializer;
import me.fixeddev.ezchat.format.NewChatFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NewChatFormatHandler implements ChatFormatHandler<AbstractChatEvent> {

    private final ChatFormatManager chatFormatManager;
    private final ChatFormatSerializer chatFormatSerializer;

    private final LegacyComponentSerializer componentSerializer;

    public NewChatFormatHandler(ChatFormatManager chatFormatManager) {
        this.chatFormatManager = chatFormatManager;

        this.chatFormatSerializer = new ChatFormatSerializer();

        componentSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().extractUrls().build();
    }

    @Override
    public void accept(AbstractChatEvent event) {
        Player player = event.getPlayer();

        NewChatFormat chatFormat = chatFormatManager.getChatFormatForPlayer(player).copy();

        String legacyMessage = componentSerializer.serialize(event.message());

        if (player.hasPermission("ezchat.color")) {
            legacyMessage = ChatColor.translateAlternateColorCodes('&', legacyMessage);
        }

        Component chatMessage = componentSerializer.deserialize(ChatFormatSerializer.replacePlaceholders(player, chatFormat.getChatColor()) + legacyMessage);
        event.message(chatMessage);

        AsyncEzChatEvent chatEvent = new AsyncEzChatEvent(event, chatFormat);

        Bukkit.getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        if (chatFormat.usingPlaceholderApi()) {
            event.renderer((source, sourceDisplayName, message, viewer) -> {
                Component completeMessage = componentSerializer.deserialize(ChatFormatSerializer.replacePlaceholders(player, chatFormat.getChatColor())).append(message);

                return chatFormatSerializer.constructJsonMessage(chatFormat, player, viewer).append(completeMessage);
            });
        } else {
            event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> {
                Component completeMessage = componentSerializer.deserialize(ChatFormatSerializer.replacePlaceholders(player, chatFormat.getChatColor())).append(message);

                return chatFormatSerializer.constructJsonMessage(chatFormat, player).append(completeMessage);
            }));
        }
    }
}
