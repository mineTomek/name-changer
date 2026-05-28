package dev.minetomek.namechanger.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import dev.minetomek.namechanger.NameChanger;
import dev.minetomek.namechanger.NameChangerConfig;
import dev.minetomek.namechanger.name.NameConflict;
import dev.minetomek.namechanger.name.NameResolver;
import net.blay09.mods.balm.Balm;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import java.util.Objects;
import java.util.Optional;

public class NameChangeCommand {
    private static final Dynamic3CommandExceptionType ERROR_NAME_CONFLICT_ORIGINAL = new Dynamic3CommandExceptionType(
            (originalName, playerProfileName, playerCustomName) ->
                    Component.translatable(
                            "commands.name.set.failed.conflict.original",
                            originalName,
                            playerProfileName,
                            playerCustomName));
    private static final Dynamic3CommandExceptionType ERROR_NAME_CONFLICT_CUSTOM = new Dynamic3CommandExceptionType(
            (playerProfileName, playerCustomName, customName) ->
                    Component.translatable(
                            "commands.name.set.failed.conflict.custom",
                            playerProfileName,
                            playerCustomName,
                            customName));

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

    private static int set(ServerPlayer player, Component name, CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Component originalName = player.getName();

        NameChanger.LOGGER.debug("Changing player's name from {} to {}", originalName, name);

        handleNameConflicts(player, name, context);

        player.setCustomName(name);

        NameChanger.LOGGER.debug("The player's name is now {} (custom name: {})", player.getName(), player.getCustomName());

        NameChanger.updateServerMotd(player);

        context.getSource().sendSuccess(() -> Component.translatable("commands.name.set.success", originalName, player.getName()), true);

        return 1;
    }

    private static void handleNameConflicts(ServerPlayer player, Component name, CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Optional<NameConflict> nameConflict = NameResolver.findFirstConflict(player, name);

        if (nameConflict.isEmpty()) {
            return;
        }
        
        NameConflict conflict = nameConflict.get();
        NameChangerConfig config = Balm.config().getActiveConfig(NameChangerConfig.class);

        assert config != null;

        if (config.forbidNameConflicts) {
            if (conflict.type().equals(NameConflict.ConflictType.ORIGINAL_NAME)) {
                throw ERROR_NAME_CONFLICT_ORIGINAL.create(
                        conflict.conflictingPlayer().getGameProfile().name(),
                        getFeedbackDisplayName(conflict.conflictingPlayer(), false),
                        getFeedbackDisplayName(conflict.conflictingPlayer(), true)
                );
            } else {
                throw ERROR_NAME_CONFLICT_CUSTOM.create(
                        getFeedbackDisplayName(conflict.conflictingPlayer(), false),
                        getFeedbackDisplayName(conflict.conflictingPlayer(), true),
                        name
                );
            }
        }

        if (config.nameConflictWarningEnabled) {
            MutableComponent warning = conflict.type().equals(NameConflict.ConflictType.ORIGINAL_NAME)
                    ? Component.translatable("commands.name.set.warning.conflict.original",
                    conflict.conflictingPlayer().getGameProfile().name(),
                    getFeedbackDisplayName(conflict.conflictingPlayer(), false),
                    getFeedbackDisplayName(conflict.conflictingPlayer(), true))
                    : Component.translatable("commands.name.set.warning.conflict.custom",
                    getFeedbackDisplayName(conflict.conflictingPlayer(), false),
                    getFeedbackDisplayName(conflict.conflictingPlayer(), true),
                    name);

            context.getSource().sendSystemMessage(warning.withColor(
                    Objects.requireNonNull(ChatFormatting.YELLOW.getColor())
            ));
        }
    }

    private static int reset(ServerPlayer player, CommandContext<CommandSourceStack> context) {
        Component originalName = player.getName();

        player.setCustomName(null);
        NameChanger.updateServerMotd(player);

        context.getSource().sendSuccess(() -> Component.translatable("commands.name.reset.success", originalName, player.getName()), true);

        return 1;
    }

    private static Component getFeedbackDisplayName(ServerPlayer player, boolean useCustomName) {
        Component displayName = useCustomName
                ? player.getDisplayName()
                : Component.literal(player.getGameProfile().name());

        return displayName.copy().withStyle((style) ->
                style.withHoverEvent(new HoverEvent.ShowText(Component.literal(player.getScoreboardName())))
        );
    }
}
