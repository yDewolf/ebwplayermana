package com.github.ydewolf.ysoulmana;

import com.github.ydewolf.ysoulmana.api.entity.EntityManaProvider;
import com.github.ydewolf.ysoulmana.api.entity.MagicEntityRegistry;
import com.github.ydewolf.ysoulmana.config.SoulManaConfig;
import com.github.ydewolf.ysoulmana.content.mana.IPlayerMana;
import com.github.ydewolf.ysoulmana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ysoulmana.content.mana.helpers.ManaUsageHelper;
import com.github.ydewolf.ysoulmana.network.helpers.ManaSyncHelper;
import com.github.ydewolf.ysoulmana.registry.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

public class ModCapabilitiesEvents {

    @Mod.EventBusSubscriber(modid = SoulManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
            event.add(EntityType.PLAYER, ModAttributes.MAX_MANA.get(), SoulManaConfig.baseMana);
            event.add(EntityType.PLAYER, ModAttributes.MANA_REGEN.get(), SoulManaConfig.baseManaRegen);

            for (EntityType<? extends LivingEntity> type : event.getTypes()) {
                if (type != EntityType.PLAYER && MagicEntityRegistry.isMagicEntity(type)) {
                    Optional<MagicEntityRegistry.MagicEntityConfig> config = MagicEntityRegistry.getConfig(type);
                    config.ifPresent(pool_config -> {
                        event.add(type, ModAttributes.MAX_MANA.get(), pool_config.baseMaxMana());
                        event.add(type, ModAttributes.MANA_REGEN.get(), pool_config.baseManaRegen());
                    });
                }
            }
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
            Entity entity = event.getObject();

            if (entity instanceof Player) {
                event.addCapability(
                        ResourceLocation.tryBuild(SoulManaMod.MODID, "mana_pool"),
                        new PlayerManaProvider()
                );
            } else if (entity instanceof LivingEntity livingEntity) {
                if (MagicEntityRegistry.isMagicEntity(livingEntity.getType())) {
                    event.addCapability(
                            ResourceLocation.tryBuild(SoulManaMod.MODID, "mana_pool"),
                            new EntityManaProvider()
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
                    newMana.setMaxMana(newMana.getMaxMana() * SoulManaConfig.initialMaxManaPercentOnSpawn);
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