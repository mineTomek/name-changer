package dev.minetomek.namechanger.name;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

public final class NameResolver {
    public static Optional<ServerPlayer> resolvePlayer(final String name, List<ServerPlayer> players) {
        // Match custom name first
        for (ServerPlayer serverPlayer : players) {
            if (serverPlayer.getName().getString().equalsIgnoreCase(name)) {
                return Optional.of(serverPlayer);
            }
        }

        // Then try finding by original name
        for (ServerPlayer serverPlayer : players) {
            if (serverPlayer.getGameProfile().name().equalsIgnoreCase(name)) {
                return Optional.of(serverPlayer);
            }
        }

        return Optional.empty();
    }

    public static Optional<NameConflict> findFirstConflict(ServerPlayer target, Component proposedName) {
        List<ServerPlayer> allPlayers = target.level().players();

        for (ServerPlayer player : allPlayers) {
            if (player.equals(target)) {
                continue;
            }

            // Can't use player.hasCustomName() cuz the compiler is dumb and things it's dangerous
            Component customName = player.getCustomName();
            if (customName != null && customName.getString().equalsIgnoreCase(proposedName.getString())) {
                return Optional.of(new NameConflict(
                        target,
                        player,
                        proposedName.getString(),
                        NameConflict.ConflictType.CUSTOM_NAME));
            }

            if (player.getGameProfile().name().equalsIgnoreCase(proposedName.getString())) {
                return Optional.of(new NameConflict(
                        target,
                        player,
                        proposedName.getString(),
                        NameConflict.ConflictType.ORIGINAL_NAME));
            }
        }

        return Optional.empty();
    }
}
