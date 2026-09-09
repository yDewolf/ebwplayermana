package com.github.ydewolf.ebwplayermana.events.mana;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.github.ydewolf.ebwplayermana.content.mana.PlayerManaProvider;
import com.github.ydewolf.ebwplayermana.content.mana.helpers.SpellCastHelper;
import net.minecraft.world.entity.player.Player;

public class SpellCastEventHelper {
    public static void handleCast(SpellCastEvent.Pre event) {
        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND && event.getSpell().isInstantCast()) {
            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                SpellCastHelper.handleCastManaConsumption(mana, event, player, event.getSpell().isInstantCast());
            });
        }
    }

    public static void handleCastTick(SpellCastEvent.Tick event) {
        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND) {
            if (!player.isAlive() || player.isSpectator()) {
                event.setCanceled(true);
                return;
            }

            player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                SpellCastHelper.handleCastManaConsumption(mana, event, player, event.getSpell().isInstantCast());
            });
        }
    }

    public static void handlePostCast(SpellCastEvent.Post event) {
//        if (event.getCaster() instanceof Player player && event.getSource() == SpellCastEvent.Sources.WAND) {
//            SpellCastHelper.handlePlayerManaProgression(player, event.getSpell().getCost(), event.getSpell().isInstantCast());
//        }
    }
}
