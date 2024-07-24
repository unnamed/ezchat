package me.fixeddev.ezchat.format.part.mini;

import me.fixeddev.ezchat.format.part.PartConverter;
import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class MiniChatPartConverter implements PartConverter<MiniChatPart> {
    @Override
    public Component convert(MiniChatPart part, Player player) {
        String display = PlaceholderReplacer.getInstance().replacePlaceholders(player, part.chatFormat());
        
        return MiniMessage.miniMessage().deserialize(display);
    }

    @Override
    public Component convert(MiniChatPart part, Player player, Audience viewer) {
        String display = PlaceholderReplacer.getInstance().replacePlaceholders(player, part.chatFormat());
        display = PlaceholderReplacer.getInstance().replaceRelational(player, viewer, display);

        return MiniMessage.miniMessage().deserialize(display);
    }

    @Override
    public String componentToString(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }
}
