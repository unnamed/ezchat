package me.fixeddev.ezchat.format;

import me.fixeddev.ezchat.format.part.EasyChatPartConverter;
import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import me.fixeddev.ezchat.util.ColorReplacement;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class ChatFormatSerializer {

    private final LegacyComponentSerializer componentSerializer =
            LegacyComponentSerializer.builder()
                    .hexColors()
                    .extractUrls()
                    .useUnusualXRepeatedCharacterHexFormat()
                    .build();
    private boolean paper = true;
    private final EasyChatPartConverter easyChatPartConverter = new EasyChatPartConverter();

    public Component constructJsonMessage(NewChatFormat chatFormat, Player player) {
        return chatFormat.asComponent(easyChatPartConverter.unsafeFunctionForPlayer(player))
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("{displayName}")
                        .replacement(displayName(player))
                        .build());
    }

    public Component constructJsonMessage(NewChatFormat chatFormat, Player player, Audience viewer) {
        return chatFormat.asComponent(easyChatPartConverter.unsafeFunctionForPlayer(player,
                        viewer))
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("{displayName}")
                        .replacement(displayName(player))
                        .build());
    }

    public static String replacePlaceholders(Player player, String message) {
        return PlaceholderReplacer.getInstance()
                .replacePlaceholders(player, color(message));
    }

    public static String color(String message) {
        return ColorReplacement.color(message);
    }

    private Component fromString(String text) {
        return componentSerializer.deserialize(text);
    }

    private Component displayName(Player player) {
        if (paper) {
            try {
                return player.displayName();
            } catch (NoSuchMethodError ignored) {
                paper = false;
            }
        }
        return fromString(player.getDisplayName());
    }
}