package me.fixeddev.ezchat.format.part;
import me.fixeddev.ezchat.format.part.easy.EasyChatPart;
import me.fixeddev.ezchat.format.part.easy.EasyChatPartConverter;
import me.fixeddev.ezchat.format.part.mini.MiniChatPart;
import me.fixeddev.ezchat.format.part.mini.MiniChatPartConverter;

import java.util.HashMap;
import java.util.Map;

public class PartConverterRegistry {
    private final Map<Class<?>, PartConverter<?>> partConverterMap;

    public PartConverterRegistry() {
        partConverterMap = new HashMap<>();

        registerConverter(MiniChatPart.class, new MiniChatPartConverter());
        registerConverter(EasyChatPart.class, new EasyChatPartConverter());
    }

    public <T extends ChatPart<T>> PartConverter<T> getConverter(Class<? extends T> partType) {
        return (PartConverter<T>) partConverterMap.get(partType);
    }

    public <T extends ChatPart<T>> void registerConverter(Class<? extends T> partType, PartConverter<T> partConverter) {
        partConverterMap.putIfAbsent(partType, partConverter);
    }
}
