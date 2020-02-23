package me.fixeddev.ezchat;

import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RelationalReplacingEasyTextComponent extends EasyTextComponent {
    private Player player;
    private Player playerTwo;

    public RelationalReplacingEasyTextComponent(Player player, Player playerTwo) {
        this.player = player;
        this.playerTwo = playerTwo;
    }

    @Override
    public EasyTextComponent appendWithNewLine(@NotNull String content) {
        content = PlaceholderReplacer.getInstance().replacePlaceholders(player, content);
        content = PlaceholderReplacer.getInstance().replaceRelational(player, playerTwo, content);

        return super.appendWithNewLine(content);
    }


    @Override
    public @NotNull EasyTextComponent append(@NotNull String content) {
        content = PlaceholderReplacer.getInstance().replacePlaceholders(player, content);
        content = PlaceholderReplacer.getInstance().replaceRelational(player, playerTwo, content);

        return super.append(content);
    }

    @Override
    public @NotNull EasyTextComponent setHoverShowText(@NotNull String content) {
        content = PlaceholderReplacer.getInstance().replacePlaceholders(player, content);
        content = PlaceholderReplacer.getInstance().replaceRelational(player, playerTwo, content);

        return super.setHoverShowText(content);
    }
}
