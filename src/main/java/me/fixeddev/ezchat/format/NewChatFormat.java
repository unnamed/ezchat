package me.fixeddev.ezchat.format;

import me.fixeddev.ezchat.format.part.ChatPart;
import me.fixeddev.ezchat.format.part.EasyChatPart;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
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
        Component lastComponent = null;
        for (ChatPart<?> chatPart : chatParts) {
            Component partComponent = functionConverter.apply(chatPart);

            lastComponent = lastComponent == null ? partComponent : lastComponent.append(partComponent);
        }

        return lastComponent;
    }

    public NewChatFormat copy() {
        return new NewChatFormat(name, priority, chatColor, permission, usePlaceholderApi, chatParts);
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
