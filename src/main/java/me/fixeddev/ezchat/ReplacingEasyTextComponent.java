package me.fixeddev.ezchat;

import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplacingEasyTextComponent extends EasyTextComponent {
    private Player player;

    public ReplacingEasyTextComponent(Player player) {
        this.player = player;
    }

    @Override
    public EasyTextComponent appendWithNewLine(@NotNull String content) {
        content = PlaceholderReplacer.getInstance().replacePlaceholders(player, content);

        return super.appendWithNewLine(content);
    }


    @Override
    public @NotNull EasyTextComponent append(@NotNull String content) {
        content = PlaceholderReplacer.getInstance().replacePlaceholders(player, content);

        return super.append(content);
    }

    @Override
    public @NotNull EasyTextComponent setHoverShowText(@NotNull String content) {
        content = PlaceholderReplacer.getInstance().replacePlaceholders(player, content);

        return super.setHoverShowText(content);
    }
}
