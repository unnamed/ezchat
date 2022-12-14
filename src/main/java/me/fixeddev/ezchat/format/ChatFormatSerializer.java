package me.fixeddev.ezchat.format;

import me.fixeddev.ezchat.format.part.ChatPart;
import me.fixeddev.ezchat.format.part.EasyChatPartConverter;
import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import me.fixeddev.ezchat.util.ColorReplacement;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

public class ChatFormatSerializer {

    private final LegacyComponentSerializer componentSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().extractUrls().build();
    private boolean paper = true;

    private EasyChatPartConverter easyChatPartConverter = new EasyChatPartConverter();

    public Component constructJsonMessage(NewChatFormat chatFormat, Player player) {
        return chatFormat.asComponent(easyChatPartConverter.unsafeFunctionForPlayer(player))
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("{displayName}")
                        .replacement(displayName(player)).build());
        /*
        Component prefix = fromString(color(chatFormat.getPrefix()));

        if (!chatFormat.getPrefixTooltip().isEmpty()) {
            prefix = createHover(chatFormat.getPrefixTooltip(), prefix, s -> fromString(replacePlaceholders(player, s)));
        }

        prefix = setClickAction(chatFormat.getPrefixClickAction(), prefix, replacePlaceholders(player, chatFormat.getPrefixClickActionContent()));

        Component playerName = fromString(color(chatFormat.getPlayerName().replace("{name}", player.getName())));
        playerName = playerName.replaceText(TextReplacementConfig.builder().matchLiteral("{displayName}").replacement(displayName(player)).build());

        if (!chatFormat.getPlayerNameTooltip().isEmpty()) {
            playerName = createHover(chatFormat.getPlayerNameTooltip(), playerName, s -> fromString(replacePlaceholders(player, s)));
        }

        playerName = setClickAction(chatFormat.getPlayerNameClickAction(), playerName, replacePlaceholders(player, chatFormat.getPlayerNameClickActionContent()));

        Component suffix = fromString(color(chatFormat.getSuffix()));

        if (!chatFormat.getSuffixTooltip().isEmpty()) {
            suffix = createHover(chatFormat.getSuffixTooltip(), suffix, s -> fromString(replacePlaceholders(player, s)));
        }

        suffix = setClickAction(chatFormat.getSuffixClickAction(), suffix, replacePlaceholders(player, chatFormat.getSuffixClickActionContent()));

        return prefix.append(playerName).append(suffix);*/
    }

    public Component constructJsonMessage(NewChatFormat chatFormat, Player player, Audience viewer) {
        return chatFormat.asComponent(easyChatPartConverter.unsafeFunctionForPlayer(player, viewer))
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("{displayName}")
                        .replacement(displayName(player)).build());
/*
        Component prefix = fromString(color(chatFormat.getPrefix()));
        if (!chatFormat.getPrefixTooltip().isEmpty()) {
            prefix = createHover(chatFormat.getPrefixTooltip(), prefix, s -> fromString(replacePlaceholders(s, player, viewer)));
        }

        prefix = setClickAction(chatFormat.getPrefixClickAction(), prefix, replacePlaceholders(chatFormat.getPrefixClickActionContent(), player, viewer));

        Component playerName = fromString(color(chatFormat.getPlayerName().replace("{name}", player.getName())));
        playerName = playerName.replaceText(TextReplacementConfig.builder().matchLiteral("{displayName}").replacement(displayName(player)).build());

        if (!chatFormat.getPlayerNameTooltip().isEmpty()) {
            playerName = createHover(chatFormat.getPlayerNameTooltip(), playerName, s -> fromString(replacePlaceholders(s, player, viewer)));
        }

        playerName = setClickAction(chatFormat.getPlayerNameClickAction(), playerName, replacePlaceholders(chatFormat.getPrefixClickActionContent(), player, viewer));

        Component suffix = fromString(color(chatFormat.getSuffix()));

        if (!chatFormat.getSuffixTooltip().isEmpty()) {
            suffix = createHover(chatFormat.getSuffixTooltip(), suffix, s -> fromString(replacePlaceholders(s, player, viewer)));
        }

        suffix = setClickAction(chatFormat.getSuffixClickAction(), suffix, replacePlaceholders(chatFormat.getPrefixClickActionContent(), player, viewer));
        return prefix.append(playerName).append(suffix);*/
    }

    public static String replacePlaceholders(Player player, String message) {
        return PlaceholderReplacer.getInstance().replacePlaceholders(player, color(message));
    }

    public static String color(String message) {
       return ColorReplacement.color(message);
    }

    private Component setClickAction(ClickAction action, Component textComponent, String content) {
        switch (action) {
            case OPEN_URL:
                textComponent = textComponent.clickEvent(ClickEvent.openUrl(content));
                break;
            case EXECUTE_COMMAND:
                textComponent = textComponent.clickEvent(ClickEvent.runCommand(content));
                break;
            case SUGGEST_COMMAND:
                textComponent = textComponent.clickEvent(ClickEvent.suggestCommand(content));
                break;
            default:
            case NONE:
                break;
        }

        return textComponent;
    }

    private Component createHover(List<String> hover, Component component, Function<String, Component> converterFunction) {
        TextComponent hoverComponent = Component.empty();
        for (int i = 0; i < hover.size(); i++) {
            String line = color(hover.get(i));

            if (i >= hover.size() - 1) {
                hoverComponent = hoverComponent.append(converterFunction.apply(line));
            } else {
                hoverComponent = hoverComponent.append(converterFunction.apply(line)).append(Component.text('\n'));
            }
        }

        return component.hoverEvent(HoverEvent.showText(hoverComponent));
    }

    private String replacePlaceholders(String message, Player source, Audience viewer) {
        return PlaceholderReplacer.getInstance().replaceRelational(source, viewer, message);
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
