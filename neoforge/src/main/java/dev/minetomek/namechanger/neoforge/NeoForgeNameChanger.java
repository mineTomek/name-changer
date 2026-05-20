package dev.minetomek.namechanger.neoforge;

import dev.minetomek.namechanger.NameChanger;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(NameChanger.MOD_ID)
public class NeoForgeNameChanger {
    public NeoForgeNameChanger(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        Balm.initializeMod(NameChanger.MOD_ID, context, new NameChanger());

        NeoForge.EVENT_BUS.addListener(NeoForgeNameChanger::onPlayerClone);
    }

    private static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer oldPlayer &&
                event.getEntity() instanceof ServerPlayer newPlayer) {
            NameChanger.handleCopyFromEvent(oldPlayer, newPlayer);
        }
    }
}
