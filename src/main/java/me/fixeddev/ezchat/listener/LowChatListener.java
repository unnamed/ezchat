package me.fixeddev.ezchat.listener;

import me.fixeddev.ezchat.format.ChatFormatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LowChatListener extends AbstractChatListener {

    public LowChatListener(ChatFormatManager chatFormatManager, boolean alternativeChatHandling) {
        super(chatFormatManager, alternativeChatHandling);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
        formatChat(event);
    }
}
