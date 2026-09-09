package com.github.ydewolf.ebwplayermana;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.github.ydewolf.ebwplayermana.attribute.ManaAttributes;
import com.github.ydewolf.ebwplayermana.attribute.ManaModifiers;
import com.github.ydewolf.ebwplayermana.mana.ManaCalculator;
import com.github.ydewolf.ebwplayermana.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.network.ModMessages;
import com.github.ydewolf.ebwplayermana.network.SyncManaS2CPacket;
import com.github.ydewolf.ebwplayermana.utils.AttributeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EBWManaEvents {

    public static void register(WizardryEventBus bus) {
        if (PlayerManaConfig.disablePlayerMana) { return; }
        bus.register(SpellCastEvent.Pre.class, EBWManaEvents::onCast);
        bus.register(SpellCastEvent.Tick.class, EBWManaEvents::onCastTick);
        bus.register(SpellCastEvent.Post.class, EBWManaEvents::onCastPost);
    }

    public static void onCast(SpellCastEvent.Pre event) {
        if (PlayerManaConfig.disablePlayerMana) { return; }
        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND && event.getSpell().isInstantCast()) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                float cost = (float) CastItemUtils.calcCastCost(event.getSpell(), event.getModifiers());

                if (mana.getMana() >= cost) {
                    mana.consumeMana(cost);
                    handlePlayerManaUsage(player, cost, true);
                } else {
                    event.setCanceled(true);
                }
            });
        }
    }

    public static void onCastTick(SpellCastEvent.Tick event) {
        if (PlayerManaConfig.disablePlayerMana) { return; }
        if (event.isCanceled()) {
            return;
        }
        
        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND) {
            if (!player.isAlive() || player.isSpectator()) {
                event.setCanceled(true);
                return;
            }

            if (!player.isUsingItem()) {
                event.setCanceled(true);
                return;
            }

            if (!consumeContinuousMana(player, event.getSpell(), event.getModifiers())) {
                event.setCanceled(true);
                player.stopUsingItem();
            }
        }
    }

    private static boolean consumeContinuousMana(Player player, Spell spell, SpellModifiers spellModifiers) {
        if (PlayerManaConfig.disablePlayerMana) { return true; }
        return player.getCapability(PlayerManaProvider.PLAYER_MANA).map(mana -> {
            float cost = Math.max(1, spell.getCost() / 4.0f); // Custo contínuo por tick

            if (mana.getMana() >= cost) {
                mana.consumeMana(cost);
                handlePlayerManaUsage(player, cost, false);

//                SpellModifiers modifiers = new SpellModifiers();
//                modifiers.set(SpellModifiers.COST, 0);
//                spellModifiers.combine(modifiers);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public static void handlePlayerManaUsage(Player player, float spell_cost, boolean is_instant) {
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

    public static void onCastPost(SpellCastEvent.Post event) {
        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND) {
            handlePlayerManaUsage(player, event.getSpell().getCost(), event.getSpell().isInstantCast());

            SpellModifiers modifiers = new SpellModifiers();
            modifiers.set(SpellModifiers.COST, 0);
            event.getModifiers().combine(modifiers);
        }
    }
}
