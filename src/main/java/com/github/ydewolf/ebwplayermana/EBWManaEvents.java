package com.github.ydewolf.ebwplayermana;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.github.ydewolf.ebwplayermana.mana.PlayerManaProvider; // Sua capability
import net.minecraft.world.entity.player.Player;

public class EBWManaEvents {

    public static void register(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Pre.class, EBWManaEvents::onCast);
        bus.register(SpellCastEvent.Tick.class, EBWManaEvents::onCastTick);
    }

    public static void onCast(SpellCastEvent.Pre event) {
        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND && event.getSpell().isInstantCast()) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                float cost = CastItemUtils.calcCastCost(event.getSpell(), event.getModifiers());

                if (mana.getMana() >= cost) {
                    mana.consumeMana(cost);

                    // Zera o custo no EBW para a varinha não consumir os itens/recursos dela
                    SpellModifiers modifiers = new SpellModifiers();
                    modifiers.set(SpellModifiers.COST, 0);
                    event.getModifiers().combine(modifiers);
                } else {
                    event.setCanceled(true); // Cancela o conjuramento por falta de mana
                }
            });
        }
    }

    public static void onCastTick(SpellCastEvent.Tick event) {
        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND) {
            if (!consumeContinuousMana(player, event.getSpell(), event.getModifiers())) {
                event.setCanceled(true);
            }
        }
    }

    private static boolean consumeContinuousMana(Player player, Spell spell, SpellModifiers spellModifiers) {
        return player.getCapability(PlayerManaProvider.PLAYER_MANA).map(mana -> {
            float cost = Math.max(1, spell.getCost() / 4.0f); // Custo contínuo por tick

            if (mana.getMana() >= cost) {
                mana.consumeMana(cost);

                SpellModifiers modifiers = new SpellModifiers();
                modifiers.set(SpellModifiers.COST, 0);
                spellModifiers.combine(modifiers);
                return true;
            }
            return false;
        }).orElse(false);
    }
}