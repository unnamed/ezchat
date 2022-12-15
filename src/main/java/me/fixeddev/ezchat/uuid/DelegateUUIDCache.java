package me.fixeddev.ezchat.uuid;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class DelegateUUIDCache implements UUIDCache {

    private Supplier<UUIDCache> uuidCache;

    public DelegateUUIDCache(Supplier<UUIDCache> uuidCache) {
        this.uuidCache = uuidCache;
    }

    public DelegateUUIDCache(UUIDCache cache) {
        this.uuidCache = () -> cache;
    }

    @Override
    public void cache(String name, UUID id) {
        uuidCache.get().cache(name, id);
    }

    @Override
    public Optional<UUID> getId(String name) {
        return uuidCache.get().getId(name);
    }

    @Override
    public Optional<String> getName(UUID id) {
        return uuidCache.get().getName(id);
    }
}
