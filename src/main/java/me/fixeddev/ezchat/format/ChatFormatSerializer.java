package me.fixeddev.ezchat.format;

import me.fixeddev.ezchat.EasyTextComponent;
import me.fixeddev.ezchat.RelationalReplacingEasyTextComponent;
import me.fixeddev.ezchat.ReplacingEasyTextComponent;
import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatFormatSerializer {

    public BaseComponent constructJsonMessageWithoutPlaceholders(ChatFormat chatFormat, Player player) {
        EasyTextComponent prefix = new EasyTextComponent();
        prefix.append(color(chatFormat.getPrefix()));

        if (!chatFormat.getPrefixTooltip().isEmpty()) {
            EasyTextComponent hoverComponent = new EasyTextComponent();

            createHover(chatFormat.getPrefixTooltip(), prefix, hoverComponent);
        }

        setClickAction(chatFormat.getPrefixClickAction(), prefix, color(replacePlaceholders(player, chatFormat.getPrefixClickActionContent())));

        EasyTextComponent playerName = new EasyTextComponent();
        playerName.append(color(chatFormat.getPlayerName()
                .replace("{displayName}", player.getDisplayName()
                        .replace("{name}", player.getName()))));

        if (!chatFormat.getPlayerNameTooltip().isEmpty()) {
            EasyTextComponent hoverComponent = new EasyTextComponent();

            createHover(chatFormat.getPlayerNameTooltip(), playerName, hoverComponent);
        }

        setClickAction(chatFormat.getPlayerNameClickAction(), playerName, color(replacePlaceholders(player, chatFormat.getPlayerNameClickActionContent())));

        EasyTextComponent suffix = new EasyTextComponent();
        suffix.append(color(chatFormat.getSuffix()));

        if (!chatFormat.getSuffixTooltip().isEmpty()) {
            EasyTextComponent hoverComponent = new EasyTextComponent();

            createHover(chatFormat.getSuffixTooltip(), suffix, hoverComponent);
        }

        setClickAction(chatFormat.getSuffixClickAction(), suffix, color(replacePlaceholders(player, chatFormat.getSuffixClickActionContent())));

        return prefix.append(playerName).append(suffix).build();
    }

    public BaseComponent constructJsonMessage(ChatFormat chatFormat, Player player) {
        ReplacingEasyTextComponent prefix = new ReplacingEasyTextComponent(player);
        prefix.append(chatFormat.getPrefix());

        if (!chatFormat.getPrefixTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new ReplacingEasyTextComponent(player);

            createHover(chatFormat.getPrefixTooltip(), prefix, hoverComponent);
        }

        setClickAction(chatFormat.getPrefixClickAction(), prefix, replacePlaceholders(player, chatFormat.getPrefixClickActionContent()));

        ReplacingEasyTextComponent playerName = new ReplacingEasyTextComponent(player);
        playerName.append(chatFormat.getPlayerName());

        if (!chatFormat.getPlayerNameTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new ReplacingEasyTextComponent(player);

            createHover(chatFormat.getPlayerNameTooltip(), playerName, hoverComponent);
        }

        setClickAction(chatFormat.getPlayerNameClickAction(), playerName, replacePlaceholders(player, chatFormat.getPlayerNameClickActionContent()));

        ReplacingEasyTextComponent suffix = new ReplacingEasyTextComponent(player);
        suffix.append(chatFormat.getSuffix());

        if (!chatFormat.getSuffixTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new ReplacingEasyTextComponent(player);

            createHover(chatFormat.getSuffixTooltip(), suffix, hoverComponent);
        }

        setClickAction(chatFormat.getSuffixClickAction(), suffix, replacePlaceholders(player, chatFormat.getSuffixClickActionContent()));
        return prefix.append(playerName).append(suffix).build();
    }

    public BaseComponent constructJsonMessage(ChatFormat chatFormat, Player player, Player playerTwo) {
        ReplacingEasyTextComponent prefix = new RelationalReplacingEasyTextComponent(player, playerTwo);
        prefix.append(chatFormat.getPrefix());

        if (!chatFormat.getPrefixTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new RelationalReplacingEasyTextComponent(player, playerTwo);

            createHover(chatFormat.getPrefixTooltip(), prefix, hoverComponent);
        }

        setClickAction(chatFormat.getPrefixClickAction(), prefix, replacePlaceholders(player, chatFormat.getPrefixClickActionContent()));

        ReplacingEasyTextComponent playerName = new RelationalReplacingEasyTextComponent(player, playerTwo);
        playerName.append(chatFormat.getPlayerName());

        if (!chatFormat.getPlayerNameTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new RelationalReplacingEasyTextComponent(player, playerTwo);

            createHover(chatFormat.getPlayerNameTooltip(), playerName, hoverComponent);
        }

        setClickAction(chatFormat.getPlayerNameClickAction(), playerName, replacePlaceholders(player, chatFormat.getPlayerNameClickActionContent()));

        ReplacingEasyTextComponent suffix = new RelationalReplacingEasyTextComponent(player, playerTwo);
        suffix.append(chatFormat.getSuffix());

        if (!chatFormat.getSuffixTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new RelationalReplacingEasyTextComponent(player, playerTwo);

            createHover(chatFormat.getSuffixTooltip(), suffix, hoverComponent);
        }

        setClickAction(chatFormat.getSuffixClickAction(), suffix, replacePlaceholders(player, chatFormat.getSuffixClickActionContent()));
        return prefix.append(playerName).append(suffix).build();
    }

    private String replacePlaceholders(Player player, String message) {
        return PlaceholderReplacer.getInstance().replacePlaceholders(player, message);
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void setClickAction(ClickAction action, EasyTextComponent textComponent, String content) {
        switch (action) {
            case OPEN_URL:
                textComponent.setClickOpenUrl(content);
                break;
            case EXECUTE_COMMAND:
                textComponent.setClickRunCommand(content);
                break;
            case SUGGEST_COMMAND:
                textComponent.setClickSuggestCommand(content);
                break;
            default:
            case NONE:
                break;
        }
    }

    private void createHover(List<String> hover, EasyTextComponent component, EasyTextComponent hoverComponent) {
        for (int i = 0; i < hover.size(); i++) {
            String line = hover.get(i);

            if (i >= hover.size() - 1) {
                hoverComponent.append(line);
            } else {
                hoverComponent.appendWithNewLine(line);
            }
        }

        component.setHoverShowText(hoverComponent);
    }

}
