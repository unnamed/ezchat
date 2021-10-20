package me.fixeddev.ezchat.listener;

import me.fixeddev.ezchat.format.ChatFormatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NormalChatListener extends AbstractChatListener implements Listener {

    public NormalChatListener(ChatFormatManager chatFormatManager, boolean alternativeChatHandling) {
        super(chatFormatManager, alternativeChatHandling);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
        formatChat(event);
    }
}
