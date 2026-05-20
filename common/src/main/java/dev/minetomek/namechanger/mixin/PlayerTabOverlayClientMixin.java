package dev.minetomek.namechanger.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayClientMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private Component decorateName(final PlayerInfo info, final MutableComponent name) {
        throw new AssertionError();
    }

    // TODO: Properly fix tab display name
    @Overwrite
    public Component getNameForDisplay(final PlayerInfo info) {
        PlayerTabOverlay playerTabOverlay = (PlayerTabOverlay) (Object) this;

        MutableComponent name = info.getTabListDisplayName() != null
                ? info.getTabListDisplayName().copy()
                : PlayerTeam.formatNameForTeam(info.getTeam(), Component.literal(info.getProfile().name()));

        if (minecraft.level != null) {
            Player player = minecraft.level.getPlayerByUUID(info.getProfile().id());

            if (player != null) {
                name = player.getName().copy();
            }
        }

        return this.decorateName(info, name);
    }
}
