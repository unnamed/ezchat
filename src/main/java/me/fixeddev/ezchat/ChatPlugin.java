package me.fixeddev.ezchat;

import me.fixeddev.ebcm.Command;
import me.fixeddev.ebcm.CommandManager;
import me.fixeddev.ebcm.SimpleCommandManager;
import me.fixeddev.ebcm.bukkit.BukkitAuthorizer;
import me.fixeddev.ebcm.bukkit.BukkitCommandManager;
import me.fixeddev.ebcm.bukkit.parameter.provider.BukkitModule;
import me.fixeddev.ebcm.parameter.provider.ParameterProviderRegistry;
import me.fixeddev.ebcm.parametric.ParametricCommandBuilder;
import me.fixeddev.ebcm.parametric.ReflectionParametricCommandBuilder;
import me.fixeddev.ezchat.commands.EzChatCommands;
import me.fixeddev.ezchat.format.BaseChatFormatManager;
import me.fixeddev.ezchat.format.ChatFormatManager;
import me.fixeddev.ezchat.listener.AbstractChatListener;
import me.fixeddev.ezchat.listener.HighChatListener;
import me.fixeddev.ezchat.listener.HighestChatListener;
import me.fixeddev.ezchat.listener.LowChatListener;
import me.fixeddev.ezchat.listener.LowestChatListener;
import me.fixeddev.ezchat.listener.NormalChatListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public class ChatPlugin extends JavaPlugin {

    private ChatFormatManager chatFormatManager;

    private ParameterProviderRegistry registry;
    private CommandManager commandManager;
    private ParametricCommandBuilder commandBuilder;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chatFormatManager = new BaseChatFormatManager(this);

        AbstractChatListener chatListener = getChatListener();
        if (chatListener != null) {
            getServer().getPluginManager().registerEvents(chatListener, this);
        }

        registerCommands();
    }

    private void registerCommands() {
        registry = ParameterProviderRegistry.createRegistry();

        commandManager = new SimpleCommandManager(new BukkitAuthorizer(), registry);
        commandManager = new BukkitCommandManager(commandManager, getName());

        registry.installModule(new BukkitModule());

        commandBuilder = new ReflectionParametricCommandBuilder();

        List<Command> commands = commandBuilder.fromClass(new EzChatCommands(this, chatFormatManager));

        commandManager.registerCommands(commands);
    }

    private AbstractChatListener getChatListener() {
        FileConfiguration config = getConfig();
        EventPriority eventPriority = EventPriority.valueOf(config.getString("chat-event-priority", "NORMAL"));

        switch (eventPriority) {
            case LOWEST:
                return new LowestChatListener(chatFormatManager);
            case LOW:
                return new LowChatListener(chatFormatManager);
            case NORMAL:
                return new NormalChatListener(chatFormatManager);
            case HIGH:
                return new HighChatListener(chatFormatManager);
            case HIGHEST:
                return new HighestChatListener(chatFormatManager);
            default:
                getLogger().log(Level.SEVERE, "Unknown priority {0}, disabling plugin.", eventPriority);
                this.setEnabled(false);
                break;
        }

        return null;
    }

}
