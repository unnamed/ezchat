package me.fixeddev.ezchat.replacer;

import me.clip.placeholderapi.PlaceholderAPI;
import me.fixeddev.ezchat.format.ChatFormatSerializer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlaceholderApiReplacer extends PlaceholderReplacer {

    @Override
    public String replacePlaceholders(Player player, String toReplace) {
        toReplace = toReplace.replace("{name}", player.getName());
        toReplace = toReplace.replace("{displayName}", player.getDisplayName());

        return ChatFormatSerializer.color(PlaceholderAPI.setPlaceholders(player, toReplace));
    }

    @Override
    public String replaceRelational(Player player, Audience viewer, String toReplace) {
        Optional<Player> optionalViewerPlayer = toPlayer(viewer);

        return optionalViewerPlayer
                .map(value -> ChatFormatSerializer.color(PlaceholderAPI.setRelationalPlaceholders(player, value, toReplace)))
                .orElseGet(() -> ChatFormatSerializer.color(toReplace));

    }

    private Optional<Player> toPlayer(Audience audience) {
        if (audience instanceof Player) {
            return Optional.of((Player) audience);
        }

        return audience.get(Identity.UUID)
                .map(Bukkit::getPlayer);
    }
}
