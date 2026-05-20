package dev.minetomek.namechanger.fabric;

import dev.minetomek.namechanger.NameChanger;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class FabricNameChanger implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(NameChanger.MOD_ID, FabricLoadContext.INSTANCE, new NameChanger());

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, _) ->
                NameChanger.handleCopyFromEvent(oldPlayer, newPlayer));
    }
}
