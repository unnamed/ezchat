package me.fixeddev.ezchat.format;

import me.fixeddev.ezchat.format.part.ChatPart;
import me.fixeddev.ezchat.format.part.EasyChatPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SerializableAs("new-chat-format")
public class NewChatFormat implements ConfigurationSerializable {

    private final String name;
    private int priority;
    private String chatColor = "";
    private String permission;
    private boolean usePlaceholderApi;

    private final List<ChatPart<?>> chatParts;

    public NewChatFormat(String name, int priority, String chatColor, String permission, boolean usePlaceholderApi, List<ChatPart<?>> chatParts) {
        this.name = name;
        this.priority = priority;
        this.chatColor = chatColor;
        this.permission = permission;
        this.usePlaceholderApi = usePlaceholderApi;
        this.chatParts = chatParts;
    }

    public NewChatFormat(String name) {
        this(name, 99999, "", "", false, new ArrayList<>());

        chatParts.add(new EasyChatPart("{displayName}", ClickAction.SUGGEST_COMMAND, "/msg {name}", Collections.emptyList()));
        chatParts.add(new EasyChatPart(": ", ClickAction.NONE, "", Collections.emptyList()));
    }

    public NewChatFormat(Map<String, Object> map) {
        name = (String) map.get("name");
        priority = (int) map.get("priority");
        chatColor = (String) map.getOrDefault("chat-color", "");
        permission = (String) map.getOrDefault("permission", "");
        usePlaceholderApi = (boolean) map.getOrDefault("use-placeholder-api", false);

        chatParts = (List<ChatPart<?>>) map.get("parts");
    }

    public String getFormatName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public String getChatColor() {
        return chatColor;
    }

    public String getPermission() {
        return permission;
    }

    public boolean usingPlaceholderApi() {
        return usePlaceholderApi;
    }

    public List<ChatPart<?>> getPartList() {
        return chatParts;
    }

    public void addChatPart(ChatPart<?> chatPart) {
        getPartList().add(chatPart);
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setUsePlaceholderApi(boolean usePlaceholderApi) {
        this.usePlaceholderApi = usePlaceholderApi;
    }

    public Component asComponent(Function<ChatPart<?>, Component> functionConverter) {
        Component finalComponent = Component.empty();
        Component lastComponent = finalComponent;
        for (ChatPart<?> chatPart : chatParts) {
            Component partComponent = functionConverter.apply(chatPart);
            partComponent = merge(partComponent, lastComponent);

            finalComponent = finalComponent.append(partComponent);
            lastComponent = partComponent;
        }

        return finalComponent;
    }

    private Component merge(Component current, Component other) {
        Style style = current.style().merge(other.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET, Style.Merge.of(Style.Merge.COLOR, Style.Merge.DECORATIONS, Style.Merge.FONT, Style.Merge.INSERTION));

        return current.style(style);
    }

    public NewChatFormat copy() {
        List<ChatPart<?>> copiedChatParts = new ArrayList<>(chatParts.size());

        for (ChatPart<?> chatPart : chatParts) {
            copiedChatParts.add(chatPart.copy());
        }

        return new NewChatFormat(name, priority, chatColor, permission, usePlaceholderApi, copiedChatParts);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", name);
        map.put("priority", priority);
        map.put("chat-color", chatColor);
        map.put("permission", permission);
        map.put("use-placeholder-api", usePlaceholderApi);
        map.put("parts", chatParts);

        return map;
    }
}
