package me.fixeddev.ezchat.commands;

import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.ezchat.format.ChatFormatManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Level;

@Command(names = "ezchat")
public class EzChatCommands implements CommandClass {
    private final Plugin plugin;
    private final ChatFormatManager chatFormatManager;

    public EzChatCommands(Plugin plugin, ChatFormatManager chatFormatManager) {
        this.plugin = plugin;
        this.chatFormatManager = chatFormatManager;
    }

    @Command(names = "")
    public boolean mainCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "EzChat version v" + plugin.getDescription().getVersion() + " by FixedDev.");

        return true;
    }

    @Command(names = "reload", permission = "ezchat.reload")
    public boolean reload(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Reloading EzChat chat formats.");
        try {
            chatFormatManager.reload();
            sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the EzChat chat formats!");

            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An exception occurred while reloading the chat formats!", e);

            sender.sendMessage(ChatColor.RED + "Failed to reload the chat formats!");

            return true;
        }

    }
}
