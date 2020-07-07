package me.fixeddev.ezchat.uuid;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BasicUUIDCache implements UUIDCache {

    private Map<String, UUID> nameToId = new ConcurrentHashMap<>();
    private Map<UUID, String> idToName = new ConcurrentHashMap<>();

    @Override
    public void cache(String name, UUID id) {
        nameToId.put(name, id);
        idToName.put(id, name);
    }

    @Override
    public Optional<UUID> getId(String name) {
        Player player = Bukkit.getPlayer(name);

        return Optional.ofNullable(nameToId.computeIfAbsent(name, s -> player != null ? player.getUniqueId() : null));
    }

    @Override
    public Optional<String> getName(UUID id) {
        Player player = Bukkit.getPlayer(id);

        return Optional.ofNullable(idToName.computeIfAbsent(id, s -> player != null ? player.getName() : null));
    }
}
