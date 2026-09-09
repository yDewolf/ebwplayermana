package com.github.ydewolf.ebwplayermana.command;

import com.github.ydewolf.ebwplayermana.EBWManaMod;
import com.github.ydewolf.ebwplayermana.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.ModMessages;
import com.github.ydewolf.ebwplayermana.network.SyncManaS2CPacket;
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
                // Subcomando para ADICIONAR mana (Ex: /mana add @p 50)
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2)) // Requer OP nível 2
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
                // Subcomando para DEFINIR a mana (Ex: /mana set @p 100)
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2)) // Requer OP nível 2
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
                // Subcomando para CONSULTAR a mana (Ex: /mana get @p)
                .then(Commands.literal("get")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> getMana(
                                        context.getSource(),
                                        EntityArgument.getPlayer(context, "target")
                                ))
                        )
                )
        );
    }

    private static int addMana(CommandSourceStack source, Collection<ServerPlayer> targets, float amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.addMana(amount);
                ModMessages.sendToPlayer(new SyncManaS2CPacket(mana.getMana(), mana.getMaxMana()), player);
            });
        }
        source.sendSuccess(() -> Component.translatable("commands.ebwplayermana.mana.add", amount, targets.size()), true);
        return targets.size();
    }

    private static int setMana(CommandSourceStack source, Collection<ServerPlayer> targets, float amount) {
        for (ServerPlayer player : targets) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.setMana(amount);
                ModMessages.sendToPlayer(new SyncManaS2CPacket(mana.getMana(), mana.getMaxMana()), player);
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
}