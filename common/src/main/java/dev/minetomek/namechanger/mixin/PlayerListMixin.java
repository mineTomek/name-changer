package dev.minetomek.namechanger.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Final
    @Shadow
    private List<ServerPlayer> players;

    @Overwrite
    public String[] getPlayerNamesArray() {
        String[] names = new String[players.size()];

        for (int i = 0; i < players.size(); ++i) {
            names[i] = players.get(i).getName().getString();
        }

        return names;
    }

    @Overwrite
    public @Nullable ServerPlayer getPlayerByName(final String name) {
        for (ServerPlayer serverPlayer : players) {
            if (serverPlayer.getGameProfile().name().equalsIgnoreCase(name)) {
                return serverPlayer;
            }

            if (serverPlayer.getName().getString().equalsIgnoreCase(name)) {
                return serverPlayer;
            }
        }

        return null;
    }
}
