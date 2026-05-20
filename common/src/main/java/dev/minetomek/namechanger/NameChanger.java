package dev.minetomek.namechanger;

import com.mojang.logging.LogUtils;
import dev.minetomek.namechanger.command.NameChangeCommand;
import net.blay09.mods.balm.commands.BalmCommands;
import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.balm.platform.module.BalmModule;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class NameChanger implements BalmModule {
    public static final String MOD_ID = "namechanger";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void initialize() {
        ServerPlayerCallback.Join.EVENT.register(NameChanger::updateServerMotd);
    }

    @Override
    public @NonNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "common");
    }

    @Override
    public void registerCommands(@NonNull BalmCommands commands) {
        CommandBuildContext context = Commands.createValidationContext(VanillaRegistries.createLookup());
        commands.register(dispatcher -> NameChangeCommand.register(dispatcher, context));
    }

    public static void handleCopyFromEvent(@NonNull ServerPlayer oldPlayer, @NonNull ServerPlayer newPlayer) {
        newPlayer.setCustomName(oldPlayer.getCustomName());
    }

    public static void updateServerMotd(@NonNull ServerPlayer serverPlayer) {
        ServerLevel level = serverPlayer.level();
        MinecraftServer server = level.getServer();

        if (!server.isSingleplayer()) return;

        String levelName = server.getWorldData().getLevelName();

        server.setMotd(createMotd(serverPlayer.getName().getString(), levelName));
    }

    public static @NonNull String createMotd(String hostName, String levelName) {
        return "%s - %s".formatted(hostName, levelName);
    }
}