package com.github.ydewolf.ebwplayermana.events.mana;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.github.ydewolf.ebwplayermana.content.mana.ManaSpellModifiers;
import com.github.ydewolf.ebwplayermana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.content.mana.helpers.SpellCastHelper;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpellCastEventHelper {
    private static final Map<UUID, Float> CONTINUOUS_COST_CACHE = new ConcurrentHashMap<>();

    public static void handleCast(SpellCastEvent.Pre event) {
        if (!event.getModifiers().getMultipliers().containsKey(ManaSpellModifiers.INITIAL_COST)) {
            event.getModifiers().set(ManaSpellModifiers.INITIAL_COST, CastItemUtils.calcCastCost(event.getSpell(), event.getModifiers()));
        }
        event.getModifiers().set(SpellModifiers.COST, 0);

        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND) {
            float trueCost = (float) event.getModifiers().get(ManaSpellModifiers.INITIAL_COST, -1);
            if (event.getSpell().isInstantCast()) {
                CONTINUOUS_COST_CACHE.remove(player.getUUID());
            } else {
                CONTINUOUS_COST_CACHE.put(player.getUUID(), trueCost);
            }
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                SpellCastHelper.handleCastManaConsumption(mana, event, player, event.getSpell().isInstantCast(), trueCost);
            });
        }
    }

    public static void handleCastTick(SpellCastEvent.Tick event) {
        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND) {
            if (!player.isAlive() || player.isSpectator() || !player.isUsingItem()) {
                CONTINUOUS_COST_CACHE.remove(player.getUUID());
                event.setCanceled(true);
                return;
            }

            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                float trueCost = CONTINUOUS_COST_CACHE.computeIfAbsent(
                    player.getUUID(),
                    id -> (float) event.getModifiers().get(ManaSpellModifiers.INITIAL_COST, -1)
                );

                SpellCastHelper.handleCastManaConsumption(mana, event, player, false, trueCost / 4.0f);
            });
        }
    }

    public static void handlePostCast(SpellCastEvent.Post event) {
        if (event.getCaster() instanceof Player player) {
            event.getModifiers().set(SpellModifiers.COST, 0);

            if (event.isCanceled()) {
                CONTINUOUS_COST_CACHE.remove(player.getUUID());
            }
        }
    }
}
