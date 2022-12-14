package me.fixeddev.ezchat.format.part;

import me.fixeddev.ezchat.format.ClickAction;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SerializableAs("chat-part")
public class EasyChatPart implements ChatPart<EasyChatPart> {

    private String display;
    private ClickAction clickAction;
    private String clickContent;
    private List<String> tooltip;

    private static final Pattern ACTION_CONTENT = Pattern.compile("\\[(\\w+)\\] (.+)", Pattern.DOTALL);

    public EasyChatPart(String display, ClickAction clickAction, String clickContent, List<String> tooltip) {
        this.display = display;
        this.clickAction = clickAction;
        this.clickContent = clickContent;
        this.tooltip = tooltip;
    }

    @SuppressWarnings("unchecked")
    public EasyChatPart(Map<String, Object> map) {
        display = (String) map.get("display");
        String clickAction = (String) map.getOrDefault("prefix-click-action", "NONE");

        Matcher matcher = ACTION_CONTENT.matcher(clickAction);

        if (matcher.matches()) {
            String action = matcher.group(1).replace("[", "").replace("]", "");
            clickContent = clickAction.replace("[" + action + "]", "").trim();

            if (!action.isEmpty()) {
                this.clickAction = ClickAction.valueOf(action).getAliasOf();
            }
        }

        tooltip = (List<String>) map.getOrDefault("tooltip", new ArrayList<>());
    }

    public String getDisplay() {
        return display;
    }

    public ClickAction getClickAction() {
        return clickAction;
    }

    public String getClickContent() {
        return clickContent;
    }

    public List<String> getTooltip() {
        return tooltip;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public void setClickContent(String clickContent) {
        this.clickContent = clickContent;
    }

    public void setTooltip(List<String> tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public Component toComponent(Function<EasyChatPart, Component> functionConverter) {
        return functionConverter.apply(this);
    }

    @Override
    public EasyChatPart copy() {
        return new EasyChatPart(display, clickAction, clickContent, tooltip);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("display", display);
        map.put("click-action", "[" + clickAction.getShortVersion() + "] " + clickContent);
        map.put("tooltip", tooltip);

        return map;
    }
}
