package me.fixeddev.ezchat.format;

import me.fixeddev.ezchat.EasyTextComponent;
import me.fixeddev.ezchat.ReplacingEasyTextComponent;
import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class ChatFormatSerializer {

    private final static Pattern HEX_COLOR_PATTERN = Pattern.compile("&\\[([\\dA-Fa-f])([\\dA-Fa-f])," +
            "([\\dA-Fa-f])([\\dA-Fa-f])," +
            "([\\dA-Fa-f])([\\dA-Fa-f])]");
    private final static Pattern SECONDARY_HEX_COLOR_PATTERN = Pattern.compile("&?#([\\dA-Fa-f]{2})([\\dA-Fa-f]{2})([\\dA-Fa-f]{2})");
    private final static String BUKKIT_HEX_COLOR = ChatColor.COLOR_CHAR + "x" +
            ChatColor.COLOR_CHAR + "$1" +
            ChatColor.COLOR_CHAR + "$2" +
            ChatColor.COLOR_CHAR + "$3" +
            ChatColor.COLOR_CHAR + "$4" +
            ChatColor.COLOR_CHAR + "$5" +
            ChatColor.COLOR_CHAR + "$6";
    private final static String EZ_HEX_COLOR_REPLACEMENT = "&[$1,$2,$3]";

    public EasyTextComponent constructJsonMessage(ChatFormat chatFormat, Player player) {
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

        return prefix.append(playerName).append(suffix);
    }

    public EasyTextComponent constructJsonMessage(ChatFormat chatFormat, Player player, Player playerTwo) {
        ReplacingEasyTextComponent prefix = new ReplacingEasyTextComponent(player, playerTwo);
        prefix.append(chatFormat.getPrefix());

        if (!chatFormat.getPrefixTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new ReplacingEasyTextComponent(player, playerTwo);

            createHover(chatFormat.getPrefixTooltip(), prefix, hoverComponent);
        }

        setClickAction(chatFormat.getPrefixClickAction(), prefix, replacePlaceholders(player, chatFormat.getPrefixClickActionContent()));

        ReplacingEasyTextComponent playerName = new ReplacingEasyTextComponent(player, playerTwo);
        playerName.append(chatFormat.getPlayerName());

        if (!chatFormat.getPlayerNameTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new ReplacingEasyTextComponent(player, playerTwo);

            createHover(chatFormat.getPlayerNameTooltip(), playerName, hoverComponent);
        }

        setClickAction(chatFormat.getPlayerNameClickAction(), playerName, replacePlaceholders(player, chatFormat.getPlayerNameClickActionContent()));

        ReplacingEasyTextComponent suffix = new ReplacingEasyTextComponent(player, playerTwo);
        suffix.append(chatFormat.getSuffix());

        if (!chatFormat.getSuffixTooltip().isEmpty()) {
            ReplacingEasyTextComponent hoverComponent = new ReplacingEasyTextComponent(player, playerTwo);

            createHover(chatFormat.getSuffixTooltip(), suffix, hoverComponent);
        }

        setClickAction(chatFormat.getSuffixClickAction(), suffix, replacePlaceholders(player, chatFormat.getSuffixClickActionContent()));
        return prefix.append(playerName).append(suffix);
    }

    private String replacePlaceholders(Player player, String message) {
        return PlaceholderReplacer.getInstance().replacePlaceholders(player, message);
    }

    public static String color(String message) {
        String newMessage = SECONDARY_HEX_COLOR_PATTERN.matcher(message).replaceAll(EZ_HEX_COLOR_REPLACEMENT);

        newMessage = HEX_COLOR_PATTERN.matcher(newMessage).replaceAll(BUKKIT_HEX_COLOR);

        return ChatColor.translateAlternateColorCodes('&', newMessage);
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
