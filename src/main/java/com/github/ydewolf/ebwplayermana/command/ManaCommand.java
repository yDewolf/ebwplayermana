package com.github.ydewolf.ebwplayermana.command;

import com.github.ydewolf.ebwplayermana.EBWManaMod;
import com.github.ydewolf.ebwplayermana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.content.mana.helpers.SpellCastHelper;
import com.github.ydewolf.ebwplayermana.network.helpers.ManaSyncHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ManaCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mana")
                // --- MANA ATUAL ---
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                        .executes(context -> addMana(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                FloatArgumentType.getFloat(context, "amount")
                                        ))
                                )
                        )
                )
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                        .executes(context -> setMana(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                FloatArgumentType.getFloat(context, "amount")
                                        ))
                                )
                        )
                )
                .then(Commands.literal("get")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> getMana(
                                        context.getSource(),
                                        EntityArgument.getPlayer(context, "target")
                                ))
                        )
                )
                // --- MANA GASTA (PROGRESSÃO) ---
                // Uso: /mana used add @p 100 | /mana used set @p 500 | /mana used get @p
                .then(Commands.literal("used")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("add")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                                .executes(context -> addUsedMana(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        FloatArgumentType.getFloat(context, "amount")
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                                .executes(context -> setUsedMana(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        FloatArgumentType.getFloat(context, "amount")
                                                ))
                                        )
                                )
                        )
                        .then(Commands.literal("get")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(context -> getUsedMana(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "target")
                                        ))
                                )
                        )
                )
        );
    }

    private static int addMana(CommandSourceStack source, Collection<ServerPlayer> targets, float amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.addMana(amount);
                ManaSyncHelper.syncManaToClient(player);
            });
        }
        source.sendSuccess(() -> Component.translatable("commands.ebwplayermana.mana.add", amount, targets.size()), true);
        return targets.size();
    }

    private static int setMana(CommandSourceStack source, Collection<ServerPlayer> targets, float amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.setMana(amount);
                ManaSyncHelper.syncManaToClient(player);
            });
        }
        source.sendSuccess(() -> Component.translatable("commands.ebwplayermana.mana.set", amount, targets.size()), true);
        return targets.size();
    }

    private static int getMana(CommandSourceStack source, ServerPlayer target) {
        target.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            source.sendSuccess(() -> Component.translatable("commands.ebwplayermana.mana.get", target.getDisplayName(), mana.getMana(), mana.getMaxMana()), false);
        });
        return 1;
    }

    // --- UsedMana ---

    private static int addUsedMana(CommandSourceStack source, Collection<ServerPlayer> targets, float amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.addTotalManaUsed(amount);
                SpellCastHelper.reapplyManaAttributes(player);
                ManaSyncHelper.syncManaToClient(player);
            });
        }
        source.sendSuccess(() -> Component.translatable("commands.ebwplayermana.mana.used.add", amount, targets.size()), true);
        return targets.size();
    }

    private static int setUsedMana(CommandSourceStack source, Collection<ServerPlayer> targets, float amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.setTotalManaUsed(amount);
                SpellCastHelper.reapplyManaAttributes(player);
                ManaSyncHelper.syncManaToClient(player);
            });
        }
        source.sendSuccess(() -> Component.translatable("commands.ebwplayermana.mana.used.set", amount, targets.size()), true);
        return targets.size();
    }

    private static int getUsedMana(CommandSourceStack source, ServerPlayer target) {
        target.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            source.sendSuccess(() -> Component.translatable("commands.ebwplayermana.mana.used.get", target.getDisplayName(), mana.getTotalManaUsed()), false);
        });
        return 1;
    }
}