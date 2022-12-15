package me.fixeddev.ezchat.replacer;

import me.fixeddev.ezchat.format.ChatFormatSerializer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

public class DummyReplacer extends PlaceholderReplacer {
    @Override
    public String replacePlaceholders(Player player, String toReplace) {
        toReplace = toReplace.replace("{name}", player.getName());
        toReplace = toReplace.replace("{displayName}", player.getDisplayName());

        return ChatFormatSerializer.color(toReplace);
    }

    @Override
    public String replaceRelational(Player player, Audience viewer, String toReplace) {
        return ChatFormatSerializer.color(toReplace);
    }
}
