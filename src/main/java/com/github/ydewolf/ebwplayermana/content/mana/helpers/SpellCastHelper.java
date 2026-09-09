package com.github.ydewolf.ebwplayermana.content.mana.helpers;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.github.ydewolf.ebwplayermana.PlayerManaConfig;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaAttributes;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaModifiers;
import com.github.ydewolf.ebwplayermana.content.mana.IPlayerMana;
import com.github.ydewolf.ebwplayermana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.ModMessages;
import com.github.ydewolf.ebwplayermana.network.SyncManaS2CPacket;
import com.github.ydewolf.ebwplayermana.network.helpers.ManaSyncHelper;
import com.github.ydewolf.ebwplayermana.utils.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
                ManaSyncHelper.syncManaToClient(serverPlayer);
            }
        });
    }

    public static void handleCastManaConsumption(IPlayerMana mana, SpellCastEvent event, Player player, boolean is_instant) {
        float cost = (float) CastItemUtils.calcCastCost(event.getSpell(), event.getModifiers());
        float playerCurrentMana = mana.getMana();
        float wand_cost = playerCurrentMana >= cost ? 0 : (cost - playerCurrentMana);
        if (playerCurrentMana >= cost) {
            mana.consumeMana(cost);
            SpellCastHelper.handlePlayerManaProgression(player, cost, is_instant);
            mana.setRegenCooldown(PlayerManaConfig.manaRegenCooldownAfterSpell);

        } else if (PlayerManaConfig.consumeWandIfNoPlayerMana) {
            if (wandCanCastSpell(player, wand_cost)) {
                mana.consumeMana(playerCurrentMana);
                SpellCastHelper.handlePlayerManaProgression(player, playerCurrentMana, is_instant);
            } else {
                event.setCanceled(true);
                return;
            }
        } else {
            event.setCanceled(true);
            return;
        }

//        FIXME: achar um jeito de passar o custo específico da varinha
//        O problema é que setar ele para 0 aqui faz com que o castTick não
//        tenha um custo verdadeiro
        if (is_instant) {
//            FIXME: esse if é para evitar o problema descrito em cima
            SpellModifiers modifiers = new SpellModifiers();
            modifiers.set(SpellModifiers.COST, (int) wand_cost);
            event.getModifiers().combine(modifiers);
        }
    }

    public static boolean wandCanCastSpell(Player player, float spell_cost) {
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof IManaItem wandItem) {
            if (!(wandItem instanceof ICastItem)) { return false; }

            if (wandItem.getMana(heldItem) < spell_cost) {
                return false;
            }
        }
        return true;
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
