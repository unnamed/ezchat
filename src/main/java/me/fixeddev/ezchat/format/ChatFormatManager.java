package me.fixeddev.ezchat.format;

import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Collection;

public interface ChatFormatManager {

    NewChatFormat getChatFormatForPlayer(Player player);

    NewChatFormat getChatFormatForPlayer(Player player, PriorityOrder priorityOrder);

    NewChatFormat getChatFormat(String name);

    Collection<NewChatFormat> getRegisteredChatFormats();

    void registerChatFormat(NewChatFormat chatFormat);

    void reload() throws IOException;

    void save() throws IOException;
}
