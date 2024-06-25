package me.fixeddev.ezchat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.fixeddev.ezchat.commands.CommandRegistry;
import me.fixeddev.ezchat.format.BaseChatFormatManager;
import me.fixeddev.ezchat.format.ChatFormatManager;
import me.fixeddev.ezchat.listener.ChatFormatHandler;
import me.fixeddev.ezchat.listener.NewChatFormatHandler;
import me.fixeddev.ezchat.listener.OldChatFormatHandler;
import me.fixeddev.ezchat.uuid.BasicUUIDCache;
import me.fixeddev.ezchat.uuid.DelegateUUIDCache;
import me.fixeddev.ezchat.uuid.UUIDCache;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;
import java.util.logging.Level;

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

        getLogger().log(Level.INFO, "Registering chat listener for priority " + eventPriority);
        ChatFormatHandler chatFormatHandler;
        Class<? extends PlayerEvent> eventClazz;

        if (hasClass("io.papermc.paper.event.player.AbstractChatEvent") && paperHasAdventure()) {
            chatFormatHandler = new NewChatFormatHandler(chatFormatManager);
            eventClazz = AsyncChatEvent.class;

            getLogger().log(Level.INFO, "Paper was detected, using Paper's chat event.");
        } else {
            chatFormatHandler = new OldChatFormatHandler(chatFormatManager, alternativeHandling, this);
            eventClazz = AsyncPlayerChatEvent.class;

            getLogger().log(Level.INFO, "Unable to detect Paper, using legacy chat event.");
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

    private static boolean paperHasAdventure() {
        try {
            Player.class.getDeclaredMethod("displayName", Component.class);

            return true;
        } catch (NoSuchMethodException e) {
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
