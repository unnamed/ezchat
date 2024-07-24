package me.fixeddev.ezchat.format.part.mini;

import me.fixeddev.ezchat.format.part.ChatPart;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@SerializableAs("mini-part")
public class MiniChatPart implements ChatPart<MiniChatPart> {

    private final String chatFormat;

    public MiniChatPart(String chatFormat) {
        this.chatFormat = chatFormat;
    }

    public MiniChatPart(Map<String, Object> map) {
        this.chatFormat = (String) map.get("format");
    }

    public String chatFormat() {
        return chatFormat;
    }

    @Override
    public Component toComponent(Function<MiniChatPart, Component> functionConverter) {
        return functionConverter.apply(this);
    }

    @Override
    public MiniChatPart copy() {
        return new MiniChatPart(chatFormat);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("format", chatFormat);

        return map;
    }


}
