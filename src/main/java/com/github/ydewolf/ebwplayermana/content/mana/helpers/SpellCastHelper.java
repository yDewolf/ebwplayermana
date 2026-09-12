package com.github.ydewolf.ebwplayermana.content.mana.helpers;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.item.WandItem;
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
                getCastItem(player).manaItem().consumeMana(player.getMainHandItem(), (int) wand_cost, player);
                if (!(player instanceof ServerPlayer) && wand_cost > 0) {
                    player.displayClientMessage(
                            Component.translatable("message.ebwplayermana.using_wand_mana"),
                            true
                    );
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
        if (heldItem.getItem() instanceof ICastItem castItem && castItem instanceof IManaItem manaItem) {
            return new ManaCastItem(castItem, manaItem);
        }
        return null;
    }

    public static boolean wandCanCastSpell(Player player, float spell_cost) {
        ManaCastItem castItem = getCastItem(player);
        if (castItem != null) {
//            Mana >= spell_cost -> true
            return castItem.manaItem().getMana(player.getMainHandItem()) >= spell_cost;
        }
        return false;
    }
}
