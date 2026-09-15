package com.github.ydewolf.ysoulmana.content.mana.helpers;

import com.github.ydewolf.ysoulmana.api.entity.EntityManaProvider;
import com.github.ydewolf.ysoulmana.config.SoulManaConfig;
import com.github.ydewolf.ysoulmana.api.mana.ICastManaSource;
import com.github.ydewolf.ysoulmana.api.mana.ManaSourceRegistry;
import com.github.ydewolf.ysoulmana.content.attribute.ManaModifiers;
import com.github.ydewolf.ysoulmana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ysoulmana.network.helpers.ManaSyncHelper;
import com.github.ydewolf.ysoulmana.registry.ModAttributes;
import com.github.ydewolf.ysoulmana.utils.AttributeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ManaUsageHelper {
    public static void handlePlayerManaProgression(Player player, float spell_cost, boolean is_instant) {
        if (SoulManaConfig.disablePlayerMana || !(player instanceof ServerPlayer)) { return; }

        AttributeInstance maxManaAttr = player.getAttribute(ModAttributes.MAX_MANA.get());
        AttributeInstance manaRegenAttr = player.getAttribute(ModAttributes.MANA_REGEN.get());
        if (maxManaAttr == null || manaRegenAttr == null) {
            return;
        }

        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            double manaBonus;
            double regenBonus;

            double currentBonus = AttributeUtils.getModifierValue(maxManaAttr, ManaModifiers.SPELL_PROGRESSION_MANA_UUID);
            double currentRegenBonus = AttributeUtils.getModifierValue(manaRegenAttr, ManaModifiers.SPELL_PROGRESSION_REGEN_UUID);
            if (SoulManaConfig.incrementOnManaUse) {
                double rate = is_instant ? SoulManaConfig.manaCostToMaxManaRate : SoulManaConfig.manaCostToMaxManaRateContinuous;

                manaBonus = currentBonus + (spell_cost * rate);
                regenBonus = currentRegenBonus + (spell_cost * SoulManaConfig.manaCostToManaRegen);
            } else {
                manaBonus = ManaCalculator.calculateManaBonus(mana.getTotalManaUsed());
                regenBonus = ManaCalculator.calculateRegenBonus(mana.getTotalManaUsed());
            }

            AttributeUtils.applyOrUpdateModifier(
                    maxManaAttr, ManaModifiers.SPELL_PROGRESSION_MANA_UUID,
                    "Spell Progression Mana Bonus", Math.min(manaBonus, SoulManaConfig.maxManaBonus),
                    AttributeModifier.Operation.ADDITION
            );
            AttributeUtils.applyOrUpdateModifier(
                    manaRegenAttr, ManaModifiers.SPELL_PROGRESSION_REGEN_UUID,
                    "Spell Progression Regen Bonus", Math.min(regenBonus, SoulManaConfig.maxRegenBonus
                    ));

            mana.setMaxMana((float) maxManaAttr.getValue());
            if (player instanceof ServerPlayer serverPlayer) {
                ManaSyncHelper.syncManaToClient(serverPlayer);
            }
        });
    }

    /**
     * Process mana consumption from an Entity's mana pool or from the main ManaItem the entity is using
     * Returns true if mana could be consumed and false if it couldn't.
     */
    public static boolean handleCastManaConsumption(LivingEntity entity, boolean isInstant, float trueCost) {
        return entity.getCapability(EntityManaProvider.MANA_POOL).map(manaPool -> {
            float currentMana = manaPool.getMana();
            if (currentMana >= trueCost) {
                manaPool.consumeMana(trueCost);
                manaPool.setRegenCooldown(SoulManaConfig.manaRegenCooldownAfterCast);
                if (entity instanceof Player player) {
                    handlePlayerManaProgression(player, trueCost, isInstant);
                }

                return true;
            }

//          TODO: talvez adicionar uma opção para habilitar apenas para entidades que não são o player
            if (!SoulManaConfig.allowItemManaConsumption) return false;

            float itemCost = trueCost - currentMana;
            Optional<ManaSourceRegistry.ManaSourceHolder> sourceOpt = ManaSourceRegistry.getSourceFromEntity(entity);

            if (sourceOpt.isPresent() && itemCanCast(sourceOpt.get(), itemCost)) {
                if (currentMana > 0) {
                    manaPool.consumeMana(currentMana);

                    if (entity instanceof Player player) {
                        handlePlayerManaProgression(player, currentMana, isInstant);
                    }
                }

                manaPool.setRegenCooldown(SoulManaConfig.manaRegenCooldownAfterCast);

                ManaSourceRegistry.ManaSourceHolder holder = sourceOpt.get();
                ICastManaSource source = holder.source();
                ItemStack stack = holder.stack();

                source.consumeMana(stack, itemCost, entity);
                if (entity instanceof ServerPlayer serverPlayer && itemCost > 0) {
                    serverPlayer.displayClientMessage(
                            Component.translatable("message.ysoulmana.using_item_mana", stack.getDisplayName()),
                            true
                    );
                }

                return true;
            }

            return false;
        }).orElse(false);
    }

    public static void reapplyManaAttributes(ServerPlayer player) {
        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            AttributeInstance maxManaAttr = player.getAttribute(ModAttributes.MAX_MANA.get());
            AttributeInstance manaRegenAttr = player.getAttribute(ModAttributes.MANA_REGEN.get());

            if (maxManaAttr != null && manaRegenAttr != null) {
                maxManaAttr.setBaseValue(SoulManaConfig.baseMana);
                manaRegenAttr.setBaseValue(SoulManaConfig.baseManaRegen);

                float totalUsed = mana.getTotalManaUsed();
                double manaBonus;
                double regenBonus;
                if (!SoulManaConfig.incrementOnManaUse) {
                    manaBonus = Math.min(ManaCalculator.calculateManaBonus(totalUsed), SoulManaConfig.maxManaBonus);
                    regenBonus = Math.min(ManaCalculator.calculateRegenBonus(totalUsed), SoulManaConfig.maxManaBonus);
                } else {
//                  FIXME: adicionar suporte para recalcular mana incremental
                    manaBonus = Math.min(totalUsed * SoulManaConfig.manaCostToMaxManaRate, SoulManaConfig.maxManaBonus);
                    regenBonus = Math.min(totalUsed * SoulManaConfig.manaCostToManaRegen, SoulManaConfig.maxRegenBonus);
                }

                AttributeUtils.applyOrUpdateModifier(maxManaAttr, ManaModifiers.SPELL_PROGRESSION_MANA_UUID, "Spell Progression Mana Bonus", manaBonus);
                AttributeUtils.applyOrUpdateModifier(manaRegenAttr, ManaModifiers.SPELL_PROGRESSION_REGEN_UUID, "Spell Progression Regen Bonus", regenBonus);

                mana.setMaxMana((float) maxManaAttr.getValue());
            }
        });
    }

    public static boolean itemCanCast(ManaSourceRegistry.ManaSourceHolder holder, float cost) {
        return holder.source().getMana(holder.stack()) >= cost;
    }
}
