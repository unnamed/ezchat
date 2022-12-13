package me.fixeddev.ezchat;

import java.io.File;
import java.net.URLClassLoader;
import java.util.function.Supplier;
import java.util.logging.Level;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.fixeddev.ezchat.commands.CommandRegistry;
import me.fixeddev.ezchat.dependency.DependencyDownloader;
import me.fixeddev.ezchat.format.BaseChatFormatManager;
import me.fixeddev.ezchat.format.ChatFormatManager;
import me.fixeddev.ezchat.listener.ChatFormatHandler;
import me.fixeddev.ezchat.listener.NewChatFormatHandler;
import me.fixeddev.ezchat.listener.OldChatFormatHandler;
import me.fixeddev.ezchat.uuid.BasicUUIDCache;
import me.fixeddev.ezchat.uuid.DelegateUUIDCache;
import me.fixeddev.ezchat.uuid.UUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatPlugin extends JavaPlugin {

    private ChatFormatManager chatFormatManager;

    private Supplier<UUIDCache> uuidCacheSupplier = BasicUUIDCache::new;

    private UUIDCache uuidCache;

    private Runnable unregisterCommands;

    @Override
    public void onDisable() {
        unregisterCommands.run();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chatFormatManager = new BaseChatFormatManager(this);
        Bukkit.getServicesManager().register(ChatFormatManager.class, chatFormatManager, this, ServicePriority.Normal);

        registerChatListener();
        registerCommands();
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

    private void registerCommands() {
        CommandRegistry registry = new CommandRegistry(this, chatFormatManager);
        registry.registerCommands();

        unregisterCommands = registry::unregisterCommands;
    }

    private void registerChatListener() {
        FileConfiguration config = getConfig();
        boolean alternativeHandling = config.getBoolean("alternative-handling", false);
        EventPriority eventPriority = EventPriority.valueOf(config.getString("chat-event-priority", "NORMAL"));

        getLogger().log(Level.INFO, "Registering chat listener for priority " + eventPriority.toString());
        ChatFormatHandler chatFormatHandler = new OldChatFormatHandler(chatFormatManager, alternativeHandling);
        Class<? extends PlayerEvent> eventClazz = AsyncPlayerChatEvent.class;

        if (hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration")) {
            chatFormatHandler = new NewChatFormatHandler(chatFormatManager);
            eventClazz = AsyncChatEvent.class;

            getLogger().log(Level.INFO, "Paper was detected, using Paper's chat event.");
        } else {
            getLogger().log(Level.INFO, "Paper couldn't detected, using legacy chat event.");
        }

        ChatFormatHandler finalChatFormatHandler = chatFormatHandler;

        getServer().getPluginManager().registerEvent(eventClazz, new Listener() {
        }, eventPriority, (listener, event) -> {
            finalChatFormatHandler.accept((PlayerEvent) event);
        }, this, true);
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static UUIDCache registerCache() {
        ChatPlugin plugin = JavaPlugin.getPlugin(ChatPlugin.class);

        UUIDCache uuidCache = plugin.getUuidCache();
        Bukkit.getServicesManager().register(UUIDCache.class, new DelegateUUIDCache(plugin::getUuidCache), plugin, ServicePriority.Normal);

        return uuidCache;
    }

}
