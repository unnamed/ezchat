package me.fixeddev.ezchat.format;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseChatFormatManager implements ChatFormatManager {

    private List<ChatFormat> chatFormats;

    private ChatFormat defaultFormat;

    private PriorityOrder defaultPriorityOrder;

    private JavaPlugin plugin;

    private File configFile;
    private YamlConfiguration chatConfig;

    public BaseChatFormatManager(JavaPlugin plugin) {
        this.plugin = plugin;

        ConfigurationSerialization.registerClass(ChatFormat.class);
        ConfigurationSerialization.registerClass(BaseChatFormat.class);

        configFile = new File(plugin.getDataFolder(), "formats.yml");
        try {
            chatConfig = YamlConfiguration.loadConfiguration(Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load formats configuration!", e);
            return;

        }

        chatFormats = new CopyOnWriteArrayList<>();

        defaultPriorityOrder = PriorityOrder.valueOf(chatConfig.getString("default-priority-ordering", "LOWER_FIRST"));

        if(!chatConfig.isSet("default-priority-ordering")){
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
    public ChatFormat getChatFormatForPlayer(Player player) {
        return getChatFormatForPlayer(player, defaultPriorityOrder);
    }

    @Override
    public ChatFormat getChatFormatForPlayer(Player player, PriorityOrder priorityOrder) {
        Stream<ChatFormat> chatFormatStream = chatFormats.stream()
                .filter(format -> player.hasPermission(format.getPermission()));

        List<ChatFormat> sortedChatFormats;

        switch (priorityOrder) {
            case LOWER_FIRST:
                sortedChatFormats = chatFormatStream.sorted(Comparator.comparingInt(ChatFormat::getPriority)).collect(Collectors.toList());
                break;
            case HIGHER_FIRST:
                sortedChatFormats = chatFormatStream.sorted((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority())).collect(Collectors.toList());
                break;
            default: // In case that priorityChecking is null return random order
                sortedChatFormats = chatFormatStream.collect(Collectors.toList());
        }

        for (ChatFormat chatFormat : sortedChatFormats) {
            if (chatFormat == null) {
                continue;
            }

            return chatFormat;
        }

        return defaultFormat;
    }

    @Override
    public List<ChatFormat> getRegisteredChatFormats() {
        return chatFormats;
    }

    @Override
    public void registerChatFormat(ChatFormat chatFormat) {
        chatFormats.add(chatFormat);
    }

    @Override
    public void reload() throws IOException {
        chatConfig = YamlConfiguration.loadConfiguration(configFile);

        loadConfig();
    }

    @Override
    public void save() throws IOException {
        chatConfig.set("formats", chatFormats);
        chatConfig.save(configFile);
    }

    private void loadConfig() throws IOException {
        List<?> formatsRawList = chatConfig.getList("formats");
        if(formatsRawList == null){
            formatsRawList = new ArrayList<>();
        }

        List<ChatFormat> formats = new ArrayList<>();

        formatsRawList.forEach(o -> {
            if (!(o instanceof ChatFormat)) {
                return;
            }

            ChatFormat format = (ChatFormat) o;

            if (format.isUsePlaceholderApi() && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
                plugin.getLogger().log(Level.WARNING, "The format with name {0} has PlaceholderAPI enabled but PlaceholderAPI isn't installed, not loading it.", format.getFormatName());
                return;
            }

            if (format.isAllowRelationalPlaceholders() && !format.isUsePlaceholderApi()) {
                plugin.getLogger().log(Level.WARNING, "The format with name {0} has Relational Placeholders enabled but it doesn't has enabled the PlaceholderAPI support, ignoring Relational Placeholders.");
            }

            formats.add(format);
        });

        this.chatFormats = formats;

        Optional<ChatFormat> optionalDefaultFormat = formats.stream().filter(chatFormat -> chatFormat.getFormatName().equalsIgnoreCase("default")).findFirst();

        if (!optionalDefaultFormat.isPresent()) {
            plugin.getLogger().log(Level.INFO, "Default chat format doesn't exists, creating it.");

            ChatFormat format = new BaseChatFormat("default", 999999);
            defaultFormat = format;

            chatFormats.add(format);
            save();

            return;
        }

        defaultFormat = optionalDefaultFormat.get();
    }
}
