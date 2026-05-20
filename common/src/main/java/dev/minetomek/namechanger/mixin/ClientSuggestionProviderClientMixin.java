package dev.minetomek.namechanger.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

@Mixin(ClientSuggestionProvider.class)
public abstract class ClientSuggestionProviderClientMixin {
    @Final
    @Shadow
    private ClientPacketListener connection;

    @Overwrite
    public Collection<String> getOnlinePlayerNames() {
        List<String> result = Lists.newArrayList();

        for(AbstractClientPlayer player : connection.getLevel().players()) {
            result.add(player.getName().getString());
        }

        return result;
    }
}
