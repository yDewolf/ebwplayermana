package com.github.ydewolf.ysoulmana.ebwcompat.event;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.core.event.EventPriorityOrder;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.github.ydewolf.ysoulmana.SoulManaMod;
import com.github.ydewolf.ysoulmana.SoulManaConfig;
import com.github.ydewolf.ysoulmana.ebwcompat.event.helpers.SpellCastEventHelper;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoulManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EBWSpellEvents {

    public static void register(WizardryEventBus bus) {
        if (SoulManaConfig.disablePlayerMana) { return; }
        bus.register(SpellCastEvent.Pre.class, EBWSpellEvents::onCast, EventPriorityOrder.HIGHEST);
        bus.register(SpellCastEvent.Tick.class, EBWSpellEvents::onCastTick);
        bus.register(SpellCastEvent.Post.class, EBWSpellEvents::onCastPost, EventPriorityOrder.HIGHEST);
    }

    public static void onCast(SpellCastEvent.Pre event) {
        if (SoulManaConfig.disablePlayerMana) { return; }
        SpellCastEventHelper.handleCast(event);
    }

    public static void onCastTick(SpellCastEvent.Tick event) {
        if (SoulManaConfig.disablePlayerMana) { return; }
        SpellCastEventHelper.handleCastTick(event);
    }

    public static void onCastPost(SpellCastEvent.Post event) {
        if (SoulManaConfig.disablePlayerMana) { return; }
        SpellCastEventHelper.handlePostCast(event);
    }
}
