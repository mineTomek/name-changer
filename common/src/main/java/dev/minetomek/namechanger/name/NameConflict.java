package dev.minetomek.namechanger.name;

import net.minecraft.server.level.ServerPlayer;

public record NameConflict(
        ServerPlayer sourcePlayer,
        ServerPlayer conflictingPlayer,
        String conflictingName,
        ConflictType type
) {
    public enum ConflictType {
        ORIGINAL_NAME,
        CUSTOM_NAME,
    }
}
