package com.github.ydewolf.ysoulmana;

import com.github.ydewolf.ysoulmana.content.mana.IPlayerMana;
import com.github.ydewolf.ysoulmana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ysoulmana.content.mana.helpers.ManaUsageHelper;
import com.github.ydewolf.ysoulmana.network.helpers.ManaSyncHelper;
import com.github.ydewolf.ysoulmana.registry.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModCapabilitiesEvents {

    @Mod.EventBusSubscriber(modid = SoulManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, ModAttributes.MAX_MANA.get());
            event.add(EntityType.PLAYER, ModAttributes.MANA_REGEN.get());
        }

        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.register(IPlayerMana.class);
        }
    }

    @Mod.EventBusSubscriber(modid = SoulManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {

        @SubscribeEvent
        public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                if (!event.getObject().getCapability(PlayerManaProvider.PLAYER_MANA).isPresent()) {
                    event.addCapability(
                            ResourceLocation.tryBuild(SoulManaMod.MODID, "player_mana"),
                            new PlayerManaProvider()
                    );
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();

            oldPlayer.reviveCaps();
            oldPlayer.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(oldMana -> {
                newPlayer.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(newMana -> {
                    newMana.copyFrom(oldMana);
                });
            });

            oldPlayer.invalidateCaps();
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                ManaUsageHelper.reapplyManaAttributes(serverPlayer);
                ManaSyncHelper.syncManaToClient(serverPlayer);
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                ManaUsageHelper.reapplyManaAttributes(serverPlayer);
                ManaSyncHelper.syncManaToClient(serverPlayer);
            }
        }

        @SubscribeEvent
        public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                ManaSyncHelper.syncManaToClient(serverPlayer);
            }
        }
    }
}