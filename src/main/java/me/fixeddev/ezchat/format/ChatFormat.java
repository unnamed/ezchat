package me.fixeddev.ezchat.format;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ChatFormat extends ConfigurationSerializable {

    ChatFormat copy();

    String getFormatName();

    int getPriority();

    @NotNull
    String getPrefix();

    @NotNull
    ClickAction getPrefixClickAction();

    @NotNull
    String getPrefixClickActionContent();

    @NotNull
    List<String> getPrefixTooltip();

    @NotNull
    String getPlayerName();

    @NotNull
    ClickAction getPlayerNameClickAction();

    @NotNull
    String getPlayerNameClickActionContent();

    @NotNull
    List<String> getPlayerNameTooltip();

    @NotNull
    String getSuffix();

    @NotNull
    ClickAction getSuffixClickAction();

    @NotNull
    String getSuffixClickActionContent();

    @NotNull
    List<String> getSuffixTooltip();

    @NotNull
    String getChatColor();

    @NotNull
    String getPermission();

    boolean isUsePlaceholderApi();

    void setPriority(int priority);

    void setPrefix(@NotNull String prefix);

    void setPrefixClickAction(@NotNull ClickAction prefixClickAction);

    void setPrefixClickActionContent(@NotNull String prefixClickActionContent);

    void setPrefixTooltip(@NotNull List<String> prefixTooltip);

    void setPlayerName(@NotNull String playerName);

    void setPlayerNameClickAction(@NotNull ClickAction playerNameClickAction);

    void setPlayerNameClickActionContent(@NotNull String playerNameClickActionContent);

    void setPlayerNameTooltip(@NotNull List<String> playerNameTooltip);

    void setSuffix(@NotNull String suffix);

    void setSuffixClickAction(@NotNull ClickAction suffixClickAction);

    void setSuffixClickActionContent(@NotNull String suffixClickActionContent);

    void setSuffixTooltip(@NotNull List<String> suffixTooltip);

    void setPermission(@NotNull String permission);

    void setUsePlaceholderApi(boolean usePlaceholderApi);
}
