package dev.minetomek.namechanger.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.gui.screens.social.SocialInteractionsPlayerList;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.UUID;

@Mixin(SocialInteractionsPlayerList.class)
public abstract class SocialInteractionsPlayerListClientMixin extends ContainerObjectSelectionList<PlayerEntry> {
    @Final
    @Shadow
    private SocialInteractionsScreen socialInteractionsScreen;

    public SocialInteractionsPlayerListClientMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Overwrite
    private PlayerEntry makePlayerEntry(final UUID uuid, final PlayerInfo playerInfo) {
        Player player = Objects.requireNonNull(minecraft.level).getPlayerByUUID(uuid);

        String name = Objects.requireNonNull(player).getName().getString();

        Objects.requireNonNull(playerInfo);

        return new PlayerEntry(
                minecraft,
                socialInteractionsScreen,
                uuid,
                name,
                playerInfo::getSkin,
                playerInfo.hasVerifiableChat());
    }
}
