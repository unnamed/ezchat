package me.fixeddev.ezchat.uuid;

import java.util.Optional;
import java.util.UUID;

public interface UUIDCache {

    void cache(String name, UUID id);

    Optional<UUID> getId(String name);

    Optional<String> getName(UUID id);
}
