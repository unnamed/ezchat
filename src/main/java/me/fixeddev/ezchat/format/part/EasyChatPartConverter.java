package me.fixeddev.ezchat.format.part;

import me.fixeddev.ezchat.format.ClickAction;
import me.fixeddev.ezchat.replacer.PlaceholderReplacer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

import static me.fixeddev.ezchat.util.ColorReplacement.color;

public class EasyChatPartConverter implements PartConverter<EasyChatPart> {

    private final LegacyComponentSerializer componentSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().extractUrls().build();

    @Override
    public Component convert(EasyChatPart part, Player player) {
        String display = color(part.getDisplay());
        display = PlaceholderReplacer.getInstance().replacePlaceholders(player, display);

        Component partComponent = fromString(display);

        if (!part.getTooltip().isEmpty()) {
            partComponent = createHover(part.getTooltip(), partComponent, s -> fromString(PlaceholderReplacer.getInstance().replacePlaceholders(player, s)));
        }

        partComponent = setClickAction(part.getClickAction(), partComponent, PlaceholderReplacer.getInstance().replacePlaceholders(player, part.getClickContent()));

        return partComponent;
    }

    @Override
    public Component convert(EasyChatPart part, Player player, Audience viewer) {
        String display = color(part.getDisplay());
        display = PlaceholderReplacer.getInstance().replacePlaceholders(player, display);
        display = PlaceholderReplacer.getInstance().replaceRelational(player, viewer, display);

        Component partComponent = fromString(display);
        if (!part.getTooltip().isEmpty()) {
            partComponent = createHover(part.getTooltip(), partComponent, s -> {
                s = PlaceholderReplacer.getInstance().replacePlaceholders(player, s);
                s = PlaceholderReplacer.getInstance().replaceRelational(player, viewer, s);

                return fromString(s);
            });
        }

        String clickContent = PlaceholderReplacer.getInstance().replacePlaceholders(player, part.getClickContent());
        clickContent = PlaceholderReplacer.getInstance().replaceRelational(player, viewer, clickContent);

        partComponent = setClickAction(part.getClickAction(), partComponent, clickContent);

        return partComponent;
    }


    @Override
    public String componentToString(Component component) {
        return componentSerializer.serialize(component);
    }

    private Component setClickAction(ClickAction action, Component textComponent, String content) {
        switch (action) {
            case OPEN_URL:
                return textComponent.clickEvent(ClickEvent.openUrl(content));
            case EXECUTE_COMMAND:
                return textComponent.clickEvent(ClickEvent.runCommand(content));
            case SUGGEST_COMMAND:
                return textComponent.clickEvent(ClickEvent.suggestCommand(content));
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

    private Component fromString(String text) {
        return componentSerializer.deserialize(text);
    }


}
