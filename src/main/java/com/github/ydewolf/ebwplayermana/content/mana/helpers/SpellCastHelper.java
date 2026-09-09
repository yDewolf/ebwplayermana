package com.github.ydewolf.ebwplayermana.content.mana.helpers;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.github.ydewolf.ebwplayermana.PlayerManaConfig;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaAttributes;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaModifiers;
import com.github.ydewolf.ebwplayermana.content.mana.IPlayerMana;
import com.github.ydewolf.ebwplayermana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.ModMessages;
import com.github.ydewolf.ebwplayermana.network.SyncManaS2CPacket;
import com.github.ydewolf.ebwplayermana.utils.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public class SpellCastHelper {
    public static void handlePlayerManaProgression(Player player, float spell_cost, boolean is_instant) {
        if (PlayerManaConfig.disablePlayerMana) { return; }

        AttributeInstance maxManaAttr = player.getAttribute(ManaAttributes.MAX_MANA.get());
        AttributeInstance manaRegenAttr = player.getAttribute(ManaAttributes.MANA_REGEN.get());
        if (maxManaAttr != null && manaRegenAttr != null) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                mana.addTotalManaUsed(spell_cost);
                double manaBonus = 0.0;
                double regenBonus = 0.0;

                if (PlayerManaConfig.incrementOnManaUse) {
                    double rate = is_instant ? PlayerManaConfig.manaCostToMaxManaRate : PlayerManaConfig.manaCostToMaxManaRateContinuous;
                    double currentBonus = AttributeUtils.getModifierValue(maxManaAttr, ManaModifiers.SPELL_PROGRESSION_MANA_UUID);
                    double currentRegenBonus = AttributeUtils.getModifierValue(manaRegenAttr, ManaModifiers.SPELL_PROGRESSION_REGEN_UUID);

                    manaBonus = currentBonus + (spell_cost * rate);
                    regenBonus = currentRegenBonus + (spell_cost * PlayerManaConfig.manaCostToManaRegen);
                } else {
                    manaBonus = ManaCalculator.calculateManaBonus(mana.getTotalManaUsed());
                    regenBonus = ManaCalculator.calculateRegenBonus(mana.getTotalManaUsed());
                }

                AttributeUtils.applyOrUpdateModifier(
                        maxManaAttr, ManaModifiers.SPELL_PROGRESSION_MANA_UUID,
                        "Spell Progression Mana Bonus", Math.min(manaBonus, PlayerManaConfig.maxManaBonus)
                );
                AttributeUtils.applyOrUpdateModifier(
                        manaRegenAttr, ManaModifiers.SPELL_PROGRESSION_REGEN_UUID,
                        "Spell Progression Regen Bonus", Math.min(regenBonus, PlayerManaConfig.maxRegenBonus
                        ));

                mana.setMaxMana((float) maxManaAttr.getValue());
                if (player instanceof ServerPlayer serverPlayer) {
                    ModMessages.sendToPlayer(
                            new SyncManaS2CPacket(mana.getMana(), mana.getMaxMana()),
                            serverPlayer
                    );
                }
            });
        }
    }

    public static void handleCastManaConsumption(IPlayerMana mana, SpellCastEvent event, Player player, boolean is_instant) {
        float cost = (float) CastItemUtils.calcCastCost(event.getSpell(), event.getModifiers());
        float playerCurrentMana = mana.getMana();
        float wand_cost = playerCurrentMana >= cost ? 0 : (cost - playerCurrentMana);
        if (playerCurrentMana >= cost) {
            mana.consumeMana(cost);
            SpellCastHelper.handlePlayerManaProgression(player, cost, is_instant);
            mana.setRegenCooldown(PlayerManaConfig.manaRegenCooldownAfterSpell);

        } else if (playerCurrentMana > 0) {
            mana.consumeMana(playerCurrentMana);
            SpellCastHelper.handlePlayerManaProgression(player, playerCurrentMana, is_instant);
        }

        SpellModifiers modifiers = new SpellModifiers();
        modifiers.set(SpellModifiers.COST, (int) wand_cost);
        event.getModifiers().combine(modifiers);
//        TODO: Checar se a varinha consegue castar o spell com base em wand_cost
        mana.setRegenCooldown(PlayerManaConfig.manaRegenCooldownAfterSpell);
    }

//    public static boolean handleContinuousCast(Player player, Spell spell, SpellModifiers spellModifiers) {
//        if (PlayerManaConfig.disablePlayerMana) { return true; }
//        return player.getCapability(PlayerManaProvider.PLAYER_MANA).map(mana -> {
//            float cost = spell.getCost();
//            float playerCurrentMana = mana.getMana();
//            SpellModifiers modifiers = new SpellModifiers();
//
//            if (mana.getMana() >= cost) {
//                mana.consumeMana(cost);
//                handlePlayerManaProgression(player, spell.getCost(), false);
//                modifiers.set(SpellModifiers.COST, 0);
//                spellModifiers.combine(modifiers);
//
//                return true;
//            } else if (playerCurrentMana > 0) {
//                mana.consumeMana(playerCurrentMana);
//                SpellCastHelper.handlePlayerManaProgression(player, playerCurrentMana, true);
//
//                modifiers.set(SpellModifiers.COST, (int) (cost - playerCurrentMana));
//                spellModifiers.combine(modifiers);
//            }
//            return false;
//        }).orElse(false);
//    }
}
