package me.fixeddev.ezchat.format;

import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public interface ChatFormatManager {

    ChatFormat getChatFormatForPlayer(Player player);

    ChatFormat getChatFormatForPlayer(Player player, PriorityOrder priorityOrder);

    List<ChatFormat> getRegisteredChatFormats();

    void registerChatFormat(ChatFormat chatFormat);

    void reload() throws IOException;

    void save() throws IOException;
}
