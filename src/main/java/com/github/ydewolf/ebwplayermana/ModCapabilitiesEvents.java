package com.github.ydewolf.ebwplayermana;

import com.github.ydewolf.ebwplayermana.mana.IPlayerMana;
import com.github.ydewolf.ebwplayermana.mana.PlayerManaProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModCapabilitiesEvents {

    // Eventos do Bus do Mod (Registros iniciais)
    @Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.register(IPlayerMana.class);
        }
    }

    // Eventos do Bus do Forge (Ciclo de jogo, entidades, players)
    @Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {

        // Anexa a Capability a cada Player criado
        @SubscribeEvent
        public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                if (!event.getObject().getCapability(PlayerManaProvider.PLAYER_MANA).isPresent()) {
                    event.addCapability(
                            new ResourceLocation(EBWManaMod.MODID, "player_mana"),
                            new PlayerManaProvider()
                    );
                }
            }
        }

        // Mantém a mana ao renascer ou mudar de dimensão
        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            event.getOriginal().getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(oldMana -> {
                event.getEntity().getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(newMana -> {
                    newMana.copyFrom(oldMana);
                });
            });
        }

        // (Opcional) Regeneração passiva de mana por tick
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
                event.player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                    if (mana.getMana() < mana.getMaxMana()) {
                        mana.addMana(0.05f); // Regenera 1 ponto de mana a cada 20 ticks (1 segundo)
                    }
                });
            }
        }
    }
}