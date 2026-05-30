package dev.minetomek.namechanger.mixin;

import dev.minetomek.namechanger.NameChanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(PlayerEntry.class)
public abstract class PlayerEntryClientMixin extends ContainerObjectSelectionList.Entry<PlayerEntry> {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Final
    @Shadow
    private UUID id;

    @Redirect(
            method = "extractContent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"
            )
    )
    private void namechanger$extractPlayerNameComponent(
            GuiGraphicsExtractor graphics,
            Font font,
            @Nullable String str,
            int x,
            int y,
            int color) {
        Player player;

        if (minecraft.level != null) {
            player = minecraft.level.getPlayerByUUID(id);
        } else {
            player = null;
        }

        if (player == null) {
            NameChanger.LOGGER.warn("Could not find player {} while rendering social interactions entry; falling back to plain name", id);
            graphics.text(font, str, x, y, color);
            return;
        }

        graphics.text(font, player.getName(), x, y, color);
    }
}
