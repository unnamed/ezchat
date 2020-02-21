package me.fixeddev.ezchat.replacer;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderApiReplacer extends PlaceholderReplacer {
    @Override
    public String replacePlaceholders(Player player, String toReplace) {
        toReplace = toReplace.replace("{name}", player.getName());
        toReplace = toReplace.replace("{displayName}", player.getDisplayName());

        return PlaceholderAPI.setPlaceholders(player, toReplace);
    }

    @Override
    public String replaceRelational(Player player, Player playerTwo, String toReplace) {
        return PlaceholderAPI.setRelationalPlaceholders(player, playerTwo, toReplace);
    }
}
