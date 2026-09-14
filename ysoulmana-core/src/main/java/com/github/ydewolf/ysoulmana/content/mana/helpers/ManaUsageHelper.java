package com.github.ydewolf.ysoulmana.content.mana.helpers;

import com.github.ydewolf.ysoulmana.config.SoulManaConfig;
import com.github.ydewolf.ysoulmana.api.mana.ICastManaSource;
import com.github.ydewolf.ysoulmana.api.mana.ManaSourceRegistry;
import com.github.ydewolf.ysoulmana.content.attribute;
import com.github.ydewolf.ysoulmana.content.mana.IPlayerMana;
import com.github.ydewolf.ysoulmana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ysoulmana.network.helpers.ManaSyncHelper;
import com.github.ydewolf.ysoulmana.registry.ModAttributes;
import com.github.ydewolf.ysoulmana.utils.AttributeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ManaUsageHelper {
    public static void handlePlayerManaProgression(Player player, float spell_cost, boolean is_instant) {
        if (SoulManaConfig.disablePlayerMana) { return; }

        AttributeInstance maxManaAttr = player.getAttribute(ModAttributes.MAX_MANA.get());
        AttributeInstance manaRegenAttr = player.getAttribute(ModAttributes.MANA_REGEN.get());
        if (maxManaAttr == null || manaRegenAttr == null) {
            return;
        }

        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            double manaBonus;
            double regenBonus;

            double currentBonus = AttributeUtils.getModifierValue(maxManaAttr, attribute.ManaModifiers.SPELL_PROGRESSION_MANA_UUID);
            double currentRegenBonus = AttributeUtils.getModifierValue(manaRegenAttr, attribute.ManaModifiers.SPELL_PROGRESSION_REGEN_UUID);
            if (SoulManaConfig.incrementOnManaUse) {
                double rate = is_instant ? SoulManaConfig.manaCostToMaxManaRate : SoulManaConfig.manaCostToMaxManaRateContinuous;

                manaBonus = currentBonus + (spell_cost * rate);
                regenBonus = currentRegenBonus + (spell_cost * SoulManaConfig.manaCostToManaRegen);
            } else {
                manaBonus = ManaCalculator.calculateManaBonus(mana.getTotalManaUsed());
                regenBonus = ManaCalculator.calculateRegenBonus(mana.getTotalManaUsed());
            }

            AttributeUtils.applyOrUpdateModifier(
                    maxManaAttr, attribute.ManaModifiers.SPELL_PROGRESSION_MANA_UUID,
                    "Spell Progression Mana Bonus", Math.min(manaBonus, SoulManaConfig.maxManaBonus),
                    AttributeModifier.Operation.ADDITION
            );
            AttributeUtils.applyOrUpdateModifier(
                    manaRegenAttr, attribute.ManaModifiers.SPELL_PROGRESSION_REGEN_UUID,
                    "Spell Progression Regen Bonus", Math.min(regenBonus, SoulManaConfig.maxRegenBonus
                    ));

            mana.setMaxMana((float) maxManaAttr.getValue());
            if (player instanceof ServerPlayer serverPlayer) {
                ManaSyncHelper.syncManaToClient(serverPlayer);
            }
        });
    }

    /**
     * Process mana consumption from Player's mana or from the main ManaItem player is using
     * Returns true if mana could be consumed and false if it couldn't.
     */
    public static boolean handleCastManaConsumption(IPlayerMana mana, Player player, boolean isInstant, float trueCost) {
        float playerCurrentMana = mana.getMana();
        float itemCost = playerCurrentMana >= trueCost ? 0 : (trueCost - playerCurrentMana);

        if (playerCurrentMana >= trueCost) {
            mana.consumeMana(trueCost);
            handlePlayerManaProgression(player, trueCost, isInstant);
            mana.setRegenCooldown(SoulManaConfig.manaRegenCooldownAfterCast);
            return true;
        }

        if (SoulManaConfig.allowItemManaConsumption) {
            Optional<ManaSourceRegistry.ManaSourceHolder> sourceOpt = ManaSourceRegistry.getSourceFromPlayer(player);
            if (sourceOpt.isPresent() && itemCanCast(sourceOpt.get(), itemCost)) {
                if (playerCurrentMana > 0) {
                    mana.consumeMana(playerCurrentMana);
                    handlePlayerManaProgression(player, playerCurrentMana, isInstant);
                }

                mana.setRegenCooldown(SoulManaConfig.manaRegenCooldownAfterCast);

                ManaSourceRegistry.ManaSourceHolder holder = sourceOpt.get();
                ICastManaSource source = holder.source();
                ItemStack stack = holder.stack();

                source.consumeMana(stack, itemCost, player);

                if (!(player instanceof ServerPlayer) && itemCost > 0) {
                    player.displayClientMessage(
                            Component.translatable("message.ysoulmana.using_item_mana", stack.getDisplayName()),
                            true
                    );
                }
                return true;
            }
        }

        return false;
    }

    public static void reapplyManaAttributes(ServerPlayer player) {
        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            AttributeInstance maxManaAttr = player.getAttribute(ModAttributes.MAX_MANA.get());
            AttributeInstance manaRegenAttr = player.getAttribute(ModAttributes.MANA_REGEN.get());

            if (maxManaAttr != null && manaRegenAttr != null) {
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

                AttributeUtils.applyOrUpdateModifier(maxManaAttr, attribute.ManaModifiers.SPELL_PROGRESSION_MANA_UUID, "Spell Progression Mana Bonus", manaBonus);
                AttributeUtils.applyOrUpdateModifier(manaRegenAttr, attribute.ManaModifiers.SPELL_PROGRESSION_REGEN_UUID, "Spell Progression Regen Bonus", regenBonus);

                mana.setMaxMana((float) maxManaAttr.getValue());
            }
        });
    }

    public static boolean itemCanCast(ManaSourceRegistry.ManaSourceHolder holder, float cost) {
        return holder.source().getMana(holder.stack()) >= cost;
    }
}
