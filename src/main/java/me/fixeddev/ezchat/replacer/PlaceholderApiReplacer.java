package me.fixeddev.ezchat.replacer;

import me.clip.placeholderapi.PlaceholderAPI;
import me.fixeddev.ezchat.format.ChatFormatSerializer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

public class PlaceholderApiReplacer extends PlaceholderReplacer {
    @Override
    public String replacePlaceholders(Player player, String toReplace) {
        toReplace = toReplace.replace("{name}", player.getName());
        toReplace = toReplace.replace("{displayName}", player.getDisplayName());

        return ChatFormatSerializer.color(PlaceholderAPI.setPlaceholders(player, toReplace));
    }

    @Override
    public String replaceRelational(Player player, Audience viewer, String toReplace) {
        if (!(viewer instanceof Player)) {
            return ChatFormatSerializer.color(toReplace);
        }

        return ChatFormatSerializer.color(PlaceholderAPI.setRelationalPlaceholders(player, (Player) viewer, toReplace));
    }
}
