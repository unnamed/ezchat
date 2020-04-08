package me.fixeddev.ezchat.format;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BaseChatFormat implements ChatFormat {

    @NotNull
    private final String formatName;
    private int priority;

    @NotNull
    private String prefix = "";
    @NotNull
    private ClickAction prefixClickAction = ClickAction.NONE;
    @NotNull
    private String prefixClickActionContent = "";
    @NotNull
    private List<String> prefixTooltip = new ArrayList<>();

    @NotNull
    private String playerName = "{displayName}";
    @NotNull
    private ClickAction playerNameClickAction = ClickAction.NONE;
    @NotNull
    private String playerNameClickActionContent = "";
    @NotNull
    private List<String> playerNameTooltip = new ArrayList<>();

    @NotNull
    private String suffix = ": ";
    @NotNull
    private ClickAction suffixClickAction = ClickAction.NONE;
    @NotNull
    private String suffixClickActionContent = "";
    @NotNull
    private List<String> suffixTooltip = new ArrayList<>();

    @NotNull
    private String chatColor = "";

    @NotNull
    private String permission;

    private boolean usePlaceholderApi;

    private static final Pattern ACTION_CONTENT = Pattern.compile("\\[(\\w+)\\] [\\s\\S+]");

    @SuppressWarnings("unchecked")
    public BaseChatFormat(@NotNull Map<String, Object> map) {
        formatName = (String) map.get("name");
        priority = (int) map.get("priority");

        prefix = (String) map.getOrDefault("prefix", "");
        prefixClickActionContent = (String) map.get("prefix-click-action-content");
        String prefixClick = (String) map.getOrDefault("prefix-click-action", "NONE");

        // New format loading
        if (prefixClickActionContent == null) {
            Matcher matcher = ACTION_CONTENT.matcher(prefixClick);

            if (matcher.matches()) {
                String action = matcher.group(1).replace("[", "").replace("]", "");
                prefixClickActionContent = prefixClick.replace("[" + action + "]", "");

                if (!action.isEmpty()) {
                    prefixClickAction = ClickAction.valueOf(action).getAliasOf();
                }
            }
        } else {
            prefixClickAction = ClickAction.valueOf(prefixClick);
            prefixClickActionContent = "";
        }

        prefixTooltip = (List<String>) map.getOrDefault("prefix-tooltip", prefixTooltip);

        playerName = (String) map.getOrDefault("player-name", "");
        String playerNameClick = (String) map.getOrDefault("player-name-click-action", "NONE");
        playerNameClickActionContent = (String) map.get("player-name-click-action-content");

        // New format loading
        if (playerNameClickActionContent == null) {
            Matcher matcher = ACTION_CONTENT.matcher(playerNameClick);

            if (matcher.matches()) {
                String action = matcher.group(1).replace("[", "").replace("]", "");
                playerNameClickActionContent = playerNameClick.replace("[" + action + "]", "");

                if (!action.isEmpty()) {
                    playerNameClickAction = ClickAction.valueOf(action).getAliasOf();
                }
            }
        } else {
            playerNameClickAction = ClickAction.valueOf(playerNameClick);
            playerNameClickActionContent = "";
        }

        playerNameTooltip = (List<String>) map.getOrDefault("player-name-tooltip", prefixTooltip);

        suffix = (String) map.getOrDefault("suffix", "");
        suffixClickActionContent = (String) map.get("suffix-click-action-content");
        String suffixClick = (String) map.getOrDefault("suffix-click-action", "NONE");

        // New format loading
        if (suffixClickActionContent == null) {
            Matcher matcher = ACTION_CONTENT.matcher(suffixClick);

            if (matcher.matches()) {
                String action = matcher.group(1).replace("[", "").replace("]", "");
                suffixClickActionContent = suffixClick.replace("[" + action + "]", "");

                if (!action.isEmpty()) {
                    suffixClickAction = ClickAction.valueOf(action).getAliasOf();
                }
            }
        } else {
            suffixClickAction = ClickAction.valueOf(suffixClick);
            suffixClickActionContent = "";
        }

        suffixTooltip = (List<String>) map.getOrDefault("suffix-tooltip", suffixTooltip);

        chatColor = (String) map.getOrDefault("chat-color", "");

        permission = (String) map.getOrDefault("permission", "base.chatformat." + formatName);
        usePlaceholderApi = (boolean) map.getOrDefault("use-placeholderapi", false);
    }

    public BaseChatFormat(@NotNull ChatFormat format) {
        this.formatName = format.getFormatName();
        this.priority = format.getPriority();
        this.permission = format.getPermission();
        this.usePlaceholderApi = format.isUsePlaceholderApi();

        prefix = format.getPrefix();
        prefixClickAction = format.getPrefixClickAction();
        prefixClickActionContent = format.getPrefixClickActionContent();
        prefixTooltip = format.getPrefixTooltip();

        playerName = format.getPlayerName();
        playerNameClickAction = format.getPlayerNameClickAction();
        playerNameClickActionContent = format.getPlayerNameClickActionContent();
        playerNameTooltip = format.getPlayerNameTooltip();

        suffix = format.getSuffix();
        suffixClickAction = format.getSuffixClickAction();
        suffixClickActionContent = format.getSuffixClickActionContent();
        suffixTooltip = format.getSuffixTooltip();

        chatColor = format.getChatColor();
    }

    public BaseChatFormat(@NotNull String formatName, int priority) {
        this.formatName = formatName;
        this.priority = priority;
        permission = "default";
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", formatName);
        map.put("priority", priority);

        map.put("prefix", prefix);
        map.put("prefix-click-action", "[" + prefixClickAction.getShortVersion() + "] " + prefixClickActionContent);
        map.put("prefix-tooltip", prefixTooltip);

        map.put("player-name", playerName);
        map.put("player-name-click-action", "[" + playerNameClickAction.getShortVersion() + "] " + playerNameClickActionContent);
        map.put("player-name-tooltip", playerNameTooltip);

        map.put("suffix", suffix);
        map.put("suffix-click-action", "[" + prefixClickAction.getShortVersion() + "] " + suffixClickActionContent);
        map.put("suffix-tooltip", suffixTooltip);

        map.put("chat-color", chatColor);

        map.put("permission", permission);
        map.put("use-placeholderapi", usePlaceholderApi);

        return map;
    }

    @Override
    public ChatFormat copy() {
        return new BaseChatFormat(this);
    }

    @Override
    public String getFormatName() {
        return formatName;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NotNull
    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }

    @NotNull
    @Override
    public ClickAction getPrefixClickAction() {
        return prefixClickAction;
    }

    @Override
    public void setPrefixClickAction(@NotNull ClickAction prefixClickAction) {
        this.prefixClickAction = prefixClickAction;
    }

    @NotNull
    @Override
    public String getPrefixClickActionContent() {
        return prefixClickActionContent;
    }

    @Override
    public void setPrefixClickActionContent(@NotNull String prefixClickActionContent) {
        this.prefixClickActionContent = prefixClickActionContent;
    }

    @NotNull
    @Override
    public List<String> getPrefixTooltip() {
        return prefixTooltip;
    }

    @Override
    public void setPrefixTooltip(@NotNull List<String> prefixTooltip) {
        this.prefixTooltip = prefixTooltip;
    }

    @NotNull
    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void setPlayerName(@NotNull String playerName) {
        this.playerName = playerName;
    }

    @NotNull
    @Override
    public ClickAction getPlayerNameClickAction() {
        return playerNameClickAction;
    }

    @Override
    public void setPlayerNameClickAction(@NotNull ClickAction playerNameClickAction) {
        this.playerNameClickAction = playerNameClickAction;
    }

    @NotNull
    @Override
    public String getPlayerNameClickActionContent() {
        return playerNameClickActionContent;
    }

    @Override
    public void setPlayerNameClickActionContent(@NotNull String playerNameClickActionContent) {
        this.playerNameClickActionContent = playerNameClickActionContent;
    }

    @NotNull
    @Override
    public List<String> getPlayerNameTooltip() {
        return playerNameTooltip;
    }

    @Override
    public void setPlayerNameTooltip(@NotNull List<String> playerNameTooltip) {
        this.playerNameTooltip = playerNameTooltip;
    }

    @NotNull
    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
        this.suffix = suffix;
    }

    @NotNull
    @Override
    public ClickAction getSuffixClickAction() {
        return suffixClickAction;
    }

    @Override
    public void setSuffixClickAction(@NotNull ClickAction suffixClickAction) {
        this.suffixClickAction = suffixClickAction;
    }

    @NotNull
    @Override
    public String getSuffixClickActionContent() {
        return suffixClickActionContent;
    }

    @Override
    public void setSuffixClickActionContent(@NotNull String suffixClickActionContent) {
        this.suffixClickActionContent = suffixClickActionContent;
    }

    @NotNull
    @Override
    public List<String> getSuffixTooltip() {
        return suffixTooltip;
    }

    @Override
    public void setSuffixTooltip(@NotNull List<String> suffixTooltip) {
        this.suffixTooltip = suffixTooltip;
    }

    @NotNull
    @Override
    public String getChatColor() {
        return chatColor;
    }

    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
    }

    @NotNull
    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public void setPermission(@NotNull String permission) {
        this.permission = permission;
    }

    @Override
    public boolean isUsePlaceholderApi() {
        return usePlaceholderApi;
    }

    @Override
    public void setUsePlaceholderApi(boolean usePlaceholderApi) {
        this.usePlaceholderApi = usePlaceholderApi;
    }
}
