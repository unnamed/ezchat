package me.fixeddev.ezchat.commands;

import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import me.fixeddev.ezchat.format.ChatFormatManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Level;

@ACommand(names = "ezchat")
public class EzChatCommands implements CommandClass {
    private Plugin plugin;
    private ChatFormatManager chatFormatManager;

    public EzChatCommands(Plugin plugin, ChatFormatManager chatFormatManager) {
        this.plugin = plugin;
        this.chatFormatManager = chatFormatManager;
    }

    @ACommand(names = "")
    public boolean mainCommand(@Injected(true) CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "EzChat version v" + plugin.getDescription().getVersion() + " by FixedDev.");

        return true;
    }

    @ACommand(names = "reload", permission = "ezchat.reload")
    public boolean reload(@Injected(true) CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Reloading EzChat chat formats.");
        try {
            chatFormatManager.reload();
            sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the EzChat chat formats!");

            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An exception ocurred while reloading the chat formats!", e);

            sender.sendMessage(ChatColor.RED + "Failed to reload the chat formats!");

            return true;
        }

    }
}
