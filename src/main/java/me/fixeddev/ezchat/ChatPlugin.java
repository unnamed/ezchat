package me.fixeddev.ezchat;

import me.fixeddev.ebcm.Command;
import me.fixeddev.ebcm.CommandManager;
import me.fixeddev.ebcm.SimpleCommandManager;
import me.fixeddev.ebcm.bukkit.BukkitAuthorizer;
import me.fixeddev.ebcm.bukkit.BukkitCommandManager;
import me.fixeddev.ebcm.bukkit.BukkitMessager;
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
import me.fixeddev.ezchat.listener.MonitorChatListener;
import me.fixeddev.ezchat.listener.NormalChatListener;
import me.fixeddev.ezchat.uuid.BasicUUIDCache;
import me.fixeddev.ezchat.uuid.DelegateUUIDCache;
import me.fixeddev.ezchat.uuid.UUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ChatPlugin extends JavaPlugin {

    private ChatFormatManager chatFormatManager;

    private ParameterProviderRegistry registry;
    private CommandManager commandManager;
    private ParametricCommandBuilder commandBuilder;

    private Supplier<UUIDCache> uuidCacheSupplier = () -> new BasicUUIDCache();

    private UUIDCache uuidCache;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chatFormatManager = new BaseChatFormatManager(this);
        Bukkit.getServicesManager().register(ChatFormatManager.class, chatFormatManager, this, ServicePriority.Normal);

        AbstractChatListener chatListener = getChatListener();
        if (chatListener != null) {
            getServer().getPluginManager().registerEvents(chatListener, this);
        }

        registerCommands();
    }

    private void registerCommands() {
        registry = ParameterProviderRegistry.createRegistry();

        commandManager = new SimpleCommandManager(new BukkitAuthorizer(), new BukkitMessager(), registry);
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
            case MONITOR:
                return new MonitorChatListener(chatFormatManager);
            default:
                getLogger().log(Level.SEVERE, "Unknown priority {0}, disabling plugin.", eventPriority);
                this.setEnabled(false);
                break;
        }

        return null;
    }

    public static UUIDCache registerCache() {
        ChatPlugin plugin = JavaPlugin.getPlugin(ChatPlugin.class);

        UUIDCache uuidCache = plugin.getUuidCache();
        Bukkit.getServicesManager().register(UUIDCache.class, new DelegateUUIDCache(plugin::getUuidCache), plugin, ServicePriority.Normal);

        return uuidCache;
    }

    public UUIDCache getUuidCache() {
        if (uuidCache == null) {
            uuidCache = uuidCacheSupplier.get();
        }

        return uuidCache;
    }

    public void setUuidCache(UUIDCache uuidCache) {
        this.uuidCache = uuidCache;
    }

    public void setUuidCacheSupplier(Supplier<UUIDCache> cacheSupplier) {
        this.uuidCacheSupplier = cacheSupplier;
    }

    public Supplier<UUIDCache> getUuidCacheSupplier() {
        return uuidCacheSupplier;
    }
}
