package com.github.ydewolf.ebwplayermana.events;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.github.ydewolf.ebwplayermana.EBWManaMod;
import com.github.ydewolf.ebwplayermana.PlayerManaConfig;
import com.github.ydewolf.ebwplayermana.events.mana.SpellCastEventHelper;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EBWSpellEvents {

    public static void register(WizardryEventBus bus) {
        if (PlayerManaConfig.disablePlayerMana) { return; }
        bus.register(SpellCastEvent.Pre.class, EBWSpellEvents::onCast);
        bus.register(SpellCastEvent.Tick.class, EBWSpellEvents::onCastTick);
        bus.register(SpellCastEvent.Post.class, EBWSpellEvents::onCastPost);
    }

    public static void onCast(SpellCastEvent.Pre event) {
        if (PlayerManaConfig.disablePlayerMana) { return; }
        SpellCastEventHelper.handleCast(event);
    }

    public static void onCastTick(SpellCastEvent.Tick event) {
        if (PlayerManaConfig.disablePlayerMana) { return; }
        SpellCastEventHelper.handleCastTick(event);
    }

    public static void onCastPost(SpellCastEvent.Post event) {
        if (PlayerManaConfig.disablePlayerMana) { return; }
        SpellCastEventHelper.handlePostCast(event);
    }
}
