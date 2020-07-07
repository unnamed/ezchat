package me.fixeddev.ezchat.format;

import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Collection;

public interface ChatFormatManager {

    ChatFormat getChatFormatForPlayer(Player player);

    ChatFormat getChatFormatForPlayer(Player player, PriorityOrder priorityOrder);

    ChatFormat getChatFormat(String name);

    Collection<ChatFormat> getRegisteredChatFormats();

    void registerChatFormat(ChatFormat chatFormat);

    void reload() throws IOException;

    void save() throws IOException;
}
