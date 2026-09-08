package com.github.ydewolf.ebwplayermana.events;

import com.github.ydewolf.ebwplayermana.EBWManaMod;
import com.github.ydewolf.ebwplayermana.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.ModMessages;
import com.github.ydewolf.ebwplayermana.network.SyncManaS2C;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                ModMessages.sendToPlayer(
                        new SyncManaS2C(mana.getMana(), mana.getMaxMana()),
                        serverPlayer
                );
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Executa apenas no final do tick e estritamente no Servidor
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.player;

            // player.tickCount % 10 == 0 executa a cada 10 ticks (0.5 segundos)
            if (player.tickCount % 10 == 0) {
                player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                    if (mana.getMana() < mana.getMaxMana()) {
                        float oldMana = mana.getMana();

                        // Adiciona 1.0 de mana a cada meio segundo (2.0 de mana/seg)
                        mana.addMana(1.0f);

                        if (mana.getMana() != oldMana) {
                            ModMessages.sendToPlayer(
                                new SyncManaS2C(mana.getMana(), mana.getMaxMana()),
                                player
                            );
                        }
                    }
                });
            }
        }
    }
}