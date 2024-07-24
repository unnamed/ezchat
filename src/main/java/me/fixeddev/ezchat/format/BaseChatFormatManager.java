package me.fixeddev.ezchat.format;

import me.fixeddev.ezchat.format.part.easy.EasyChatPart;
import me.fixeddev.ezchat.format.part.mini.MiniChatPart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseChatFormatManager implements ChatFormatManager {

    private Map<String, NewChatFormat> formatsByName;
    private NewChatFormat defaultFormat;
    private PriorityOrder defaultPriorityOrder;

    private final JavaPlugin plugin;

    private final File configFile;
    private YamlConfiguration chatConfig;

    public BaseChatFormatManager(JavaPlugin plugin) {
        this.plugin = plugin;

        ConfigurationSerialization.registerClass(ChatFormat.class);
        ConfigurationSerialization.registerClass(BaseChatFormat.class);

        ConfigurationSerialization.registerClass(EasyChatPart.class);
        ConfigurationSerialization.registerClass(MiniChatPart.class);

        ConfigurationSerialization.registerClass(NewChatFormat.class);

        configFile = new File(plugin.getDataFolder(), "formats.yml");
        if (!configFile.exists()) {
            try {
                try {
                    Files.copy(getClass().getClassLoader().getResourceAsStream("formats.yml"), configFile.toPath());
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to copy default formats configuration!", e);

                    configFile.createNewFile();
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create formats.yml file!", e);
            }
        }

        try {
            chatConfig = YamlConfiguration.loadConfiguration(Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load formats configuration!", e);
            return;

        }

        formatsByName = new ConcurrentHashMap<>();

        defaultPriorityOrder = PriorityOrder.valueOf(chatConfig.getString("default-priority-ordering", "LOWER_FIRST"));

        if (!chatConfig.isSet("default-priority-ordering")) {
            chatConfig.set("default-priority-ordering", "LOWER_FIRST");
            try {
                chatConfig.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save the default priority ordering onto the config", e);
            }
        }

        try {
            loadConfig();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save the default format onto the config", e);
        }
    }

    @Override
    public NewChatFormat getChatFormatForPlayer(Player player) {
        return getChatFormatForPlayer(player, defaultPriorityOrder);
    }

    @Override
    public NewChatFormat getChatFormatForPlayer(Player player, PriorityOrder priorityOrder) {
        Stream<NewChatFormat> chatFormatStream = formatsByName.values().stream()
                .filter(format -> format != null && player.hasPermission(format.getPermission()));

        List<NewChatFormat> sortedChatFormats;

        switch (priorityOrder) {
            case LOWER_FIRST:
                sortedChatFormats = chatFormatStream.sorted(Comparator.comparingInt(NewChatFormat::getPriority)).collect(Collectors.toList());
                break;
            case HIGHER_FIRST:
                sortedChatFormats = chatFormatStream.sorted((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority())).collect(Collectors.toList());
                break;
            default: // In case that priorityChecking is null return random order
                sortedChatFormats = chatFormatStream.collect(Collectors.toList());
        }

        if (sortedChatFormats.isEmpty()) {
            return defaultFormat;
        }

        return sortedChatFormats.get(0);
    }

    @Override
    public NewChatFormat getChatFormat(String name) {
        return null;
    }

    @Override
    public Collection<NewChatFormat> getRegisteredChatFormats() {
        return formatsByName.values();
    }

    @Override
    public void registerChatFormat(NewChatFormat chatFormat) {
        formatsByName.put(chatFormat.getFormatName(), chatFormat);
    }

    @Override
    public void reload() throws IOException {
        chatConfig = YamlConfiguration.loadConfiguration(configFile);

        loadConfig();
    }

    @Override
    public void save() throws IOException {
        chatConfig.set("formats", new ArrayList<>(formatsByName.values()));
        chatConfig.save(configFile);
    }

    private void loadConfig() throws IOException {
        AtomicBoolean detectedOldFormats = new AtomicBoolean(false);
        defaultPriorityOrder = PriorityOrder.valueOf(chatConfig.getString("default-priority-ordering", "LOWER_FIRST"));

        List<?> formatsRawList = chatConfig.getList("formats");
        if (formatsRawList == null) {
            formatsRawList = new ArrayList<>();
        }

        Map<String, NewChatFormat> formatMap = new HashMap<>();

        formatsRawList.forEach(o -> {
            NewChatFormat format;

            if (o instanceof ChatFormat) {
                format = FormatConverter.convertFormat((ChatFormat) o);
                detectedOldFormats.set(true);
            } else if (!(o instanceof NewChatFormat)) {
                return;
            } else {
                format = (NewChatFormat) o;
            }

            if (format.usingPlaceholderApi() && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
                plugin.getLogger().log(Level.WARNING, "The format with name {0} has PlaceholderAPI enabled but PlaceholderAPI isn't installed, the placeholders will not work correctly.", format.getFormatName());
            }

            formatMap.put(format.getFormatName(), format);
        });

        this.formatsByName = new ConcurrentHashMap<>(formatMap);

        defaultFormat = formatMap.get("default");

        if (detectedOldFormats.get()) {
            plugin.getLogger().log(Level.WARNING, "Some chat formats with the old configuration structure were found and were converted");

            Files.copy(configFile.toPath(), new File(plugin.getDataFolder(), "formats.yml.bak").toPath());

            if (defaultFormat != null) {
                save();
            }
        }

        if (defaultFormat == null) {
            plugin.getLogger().log(Level.INFO, "Default chat format doesn't exists, creating it.");

            NewChatFormat format = new NewChatFormat("default");
            defaultFormat = format;

            formatsByName.put("default", format);
            save();
        }

    }
}
