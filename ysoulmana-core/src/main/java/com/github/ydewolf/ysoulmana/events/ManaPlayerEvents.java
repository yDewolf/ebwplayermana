package com.github.ydewolf.ysoulmana.events;

import com.github.ydewolf.ysoulmana.EBWManaMod;
import com.github.ydewolf.ysoulmana.content.mana.helpers.SpellCastHelper;
import com.github.ydewolf.ysoulmana.events.mana.ManaEventHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ManaPlayerEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SpellCastHelper.reapplyManaAttributes(player);
        }
    }


//    FIXME: don't regen mana when player is preparing a spell
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
//            ManaEventHelper.handleWandManaBonus(player);
            ManaEventHelper.handleManaRegen(player);
        }
    }
}