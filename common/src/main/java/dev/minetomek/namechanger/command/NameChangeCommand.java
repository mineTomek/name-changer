package dev.minetomek.namechanger.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.minetomek.namechanger.NameChanger;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class NameChangeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("name")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("name", ComponentArgument.textComponent(buildContext))
                                        .executes(context -> set(
                                                EntityArgument.getPlayer(context, "target"),
                                                ComponentArgument.getResolvedComponent(context, "name"),
                                                context)
                                        ))))
                .then(Commands.literal("reset")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> reset(
                                        EntityArgument.getPlayer(context, "target"),
                                        context)
                                ))
                        .executes(context -> reset(
                                context.getSource().getPlayerOrException(),
                                context
                        )))
        );
    }

    private static int set(ServerPlayer player, Component name, CommandContext<CommandSourceStack> context) {
        Component originalName = player.getName();

        player.setCustomName(name);

        NameChanger.updateServerMotd(player);

        context.getSource().sendSuccess(() -> Component.translatable("commands.name.set.success", originalName, player.getName()), true);

        return 1;
    }

    private static int reset(ServerPlayer player, CommandContext<CommandSourceStack> context) {
        Component originalName = player.getName();

        player.setCustomName(null);
        NameChanger.updateServerMotd(player);

        context.getSource().sendSuccess(() -> Component.translatable("commands.name.reset.success", originalName, player.getName()), true);

        return 1;
    }
}
