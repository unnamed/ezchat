package me.fixeddev.ezchat.listener;

import me.fixeddev.ezchat.format.ChatFormatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MonitorChatListener extends AbstractChatListener {
    public MonitorChatListener(ChatFormatManager chatFormatManager, boolean alternativeChatHandling) {
        super(chatFormatManager, alternativeChatHandling);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        formatChat(event);
    }
}
