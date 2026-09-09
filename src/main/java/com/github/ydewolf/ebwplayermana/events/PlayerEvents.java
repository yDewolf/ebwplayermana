package com.github.ydewolf.ebwplayermana.events;

import com.github.ydewolf.ebwplayermana.EBWManaMod;
import com.github.ydewolf.ebwplayermana.PlayerManaConfig;
import com.github.ydewolf.ebwplayermana.attribute.ManaAttributes;
import com.github.ydewolf.ebwplayermana.mana.ManaCalculator;
import com.github.ydewolf.ebwplayermana.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.ModMessages;
import com.github.ydewolf.ebwplayermana.network.SyncManaS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                AttributeInstance maxManaAttr = player.getAttribute(ManaAttributes.MAX_MANA.get());
                if (maxManaAttr != null) {
                    double updatedMaxMana = ManaCalculator.calculateMaxMana(PlayerManaConfig.baseMana, mana.getTotalManaUsed());
                    maxManaAttr.setBaseValue(updatedMaxMana);

                    mana.setMaxMana((float) maxManaAttr.getValue());
                    ModMessages.sendToPlayer(new SyncManaS2CPacket(mana.getMana(), mana.getMaxMana()), player);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.player;
            if (player.tickCount % 10 != 0) {
                return;
            }

            double regenPerHalfSecond = player.getAttributeValue(ManaAttributes.MANA_REGEN.get()) / 2.0D;
            double maxMana = player.getAttributeValue(ManaAttributes.MAX_MANA.get());

            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.setMaxMana((float) maxMana);
                if (mana.getMana() < mana.getMaxMana() && regenPerHalfSecond > 0) {
                    mana.addMana((float) regenPerHalfSecond);

                    ModMessages.sendToPlayer(
                        new SyncManaS2CPacket(mana.getMana(), mana.getMaxMana()),
                        player
                    );
                }
            });
        }
    }
}