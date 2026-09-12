package com.github.ydewolf.ebwplayermana.content.mana.helpers;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.github.ydewolf.ebwplayermana.PlayerManaConfig;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaAttributes;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaModifiers;
import com.github.ydewolf.ebwplayermana.content.mana.IPlayerMana;
import com.github.ydewolf.ebwplayermana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.helpers.ManaSyncHelper;
import com.github.ydewolf.ebwplayermana.utils.AttributeUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SpellCastHelper {
    public static void handlePlayerManaProgression(Player player, float spell_cost, boolean is_instant) {
        if (PlayerManaConfig.disablePlayerMana) { return; }

        AttributeInstance maxManaAttr = player.getAttribute(ManaAttributes.MAX_MANA.get());
        AttributeInstance manaRegenAttr = player.getAttribute(ManaAttributes.MANA_REGEN.get());
        if (maxManaAttr == null || manaRegenAttr == null) {
            return;
        }

        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            double manaBonus = 0.0;
            double regenBonus = 0.0;

            double currentBonus = AttributeUtils.getModifierValue(maxManaAttr, ManaModifiers.SPELL_PROGRESSION_MANA_UUID);
            double currentRegenBonus = AttributeUtils.getModifierValue(manaRegenAttr, ManaModifiers.SPELL_PROGRESSION_REGEN_UUID);
            if (PlayerManaConfig.incrementOnManaUse) {
                double rate = is_instant ? PlayerManaConfig.manaCostToMaxManaRate : PlayerManaConfig.manaCostToMaxManaRateContinuous;

                manaBonus = currentBonus + (spell_cost * rate);
                regenBonus = currentRegenBonus + (spell_cost * PlayerManaConfig.manaCostToManaRegen);
            } else {
                manaBonus = ManaCalculator.calculateManaBonus(mana.getTotalManaUsed());
                regenBonus = ManaCalculator.calculateRegenBonus(mana.getTotalManaUsed());
            }

            AttributeUtils.applyOrUpdateModifier(
                    maxManaAttr, ManaModifiers.SPELL_PROGRESSION_MANA_UUID,
                    "Spell Progression Mana Bonus", Math.min(manaBonus, PlayerManaConfig.maxManaBonus),
                    AttributeModifier.Operation.ADDITION
            );
            AttributeUtils.applyOrUpdateModifier(
                    manaRegenAttr, ManaModifiers.SPELL_PROGRESSION_REGEN_UUID,
                    "Spell Progression Regen Bonus", Math.min(regenBonus, PlayerManaConfig.maxRegenBonus
                    ));

            mana.setMaxMana((float) maxManaAttr.getValue());
            if (player instanceof ServerPlayer serverPlayer) {
                ManaSyncHelper.syncManaToClient(serverPlayer);
            }
        });
    }

    public static void handleCastManaConsumption(IPlayerMana mana, SpellCastEvent event, Player player, boolean is_instant, float trueCost) {
        float playerCurrentMana = mana.getMana();
        float wand_cost = playerCurrentMana >= trueCost ? 0 : (trueCost - playerCurrentMana);

        if (playerCurrentMana >= trueCost) {
            mana.consumeMana(trueCost);
            SpellCastHelper.handlePlayerManaProgression(player, trueCost, is_instant);
            mana.setRegenCooldown(PlayerManaConfig.manaRegenCooldownAfterSpell);

        } else if (PlayerManaConfig.consumeWandIfNoPlayerMana) {
            if (wandCanCastSpell(player, wand_cost)) {
                if (playerCurrentMana > 0) {
                    mana.consumeMana(playerCurrentMana);
                    SpellCastHelper.handlePlayerManaProgression(player, playerCurrentMana, is_instant);
                }

                mana.setRegenCooldown(PlayerManaConfig.manaRegenCooldownAfterSpell);
                ManaCastItem castItem = getCastItem(player);
                if ((castItem != null ? castItem.manaItem() : null) != null) {
                    castItem.manaItem().consumeMana(player.getMainHandItem(), (int) wand_cost, player);
                    if (!(player instanceof ServerPlayer) && wand_cost > 0) {
                        player.displayClientMessage(
                                Component.translatable("message.ebwplayermana.using_wand_mana", castItem.stack().getDisplayName()),
                                true
                        );
                    }
                }
            } else {
                event.setCanceled(true);
                return;
            }
        } else {
            event.setCanceled(true);
            return;
        }

        SpellModifiers modifiers = new SpellModifiers();
        modifiers.set(SpellModifiers.COST, (int) wand_cost);
        event.getModifiers().combine(modifiers);
    }

    public static ManaCastItem getCastItem(Player player) {
        ItemStack heldItem = player.getMainHandItem();
        ItemStack offhandItem = player.getOffhandItem();
        if (heldItem.getItem() instanceof ICastItem castItem && castItem instanceof IManaItem manaItem) {
            return new ManaCastItem(castItem, manaItem, heldItem);
        }

        if (offhandItem.getItem() instanceof ICastItem castItem && castItem instanceof IManaItem manaItem) {
            return new ManaCastItem(castItem, manaItem, offhandItem);
        }

        return null;
    }

    public static boolean wandCanCastSpell(Player player, float spell_cost) {
        ManaCastItem castItem = getCastItem(player);
//      FIXME: não sei, mas seria possível filtrar a varinha que está sendo usada
//          com base em se ela consegue castar ou não o spell, acho que tvlz seja quebrado
        if (castItem != null) {
            return castItem.manaItem().getMana(player.getMainHandItem()) >= spell_cost;
        }
        return false;
    }

    public static void reapplyManaAttributes(ServerPlayer player) {
        player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
            AttributeInstance maxManaAttr = player.getAttribute(ManaAttributes.MAX_MANA.get());
            AttributeInstance manaRegenAttr = player.getAttribute(ManaAttributes.MANA_REGEN.get());

            if (maxManaAttr != null && manaRegenAttr != null) {
                float totalUsed = mana.getTotalManaUsed();
                double manaBonus = 0.0;
                double regenBonus = 0.0;
                if (!PlayerManaConfig.incrementOnManaUse) {
                    manaBonus = Math.min(ManaCalculator.calculateManaBonus(totalUsed), PlayerManaConfig.maxManaBonus);
                    regenBonus = Math.min(ManaCalculator.calculateRegenBonus(totalUsed), PlayerManaConfig.maxManaBonus);
                } else {
//                  FIXME: adicionar suporte para recalcular mana incremental
                    manaBonus = Math.min(totalUsed * PlayerManaConfig.manaCostToMaxManaRate, PlayerManaConfig.maxManaBonus);
                    regenBonus = Math.min(totalUsed * PlayerManaConfig.manaCostToManaRegen, PlayerManaConfig.maxRegenBonus);
                }

                AttributeUtils.applyOrUpdateModifier(maxManaAttr, ManaModifiers.SPELL_PROGRESSION_MANA_UUID, "Spell Progression Mana Bonus", manaBonus);
                AttributeUtils.applyOrUpdateModifier(manaRegenAttr, ManaModifiers.SPELL_PROGRESSION_REGEN_UUID, "Spell Progression Regen Bonus", regenBonus);

                mana.setMaxMana((float) maxManaAttr.getValue());
            }
        });
    }
}
