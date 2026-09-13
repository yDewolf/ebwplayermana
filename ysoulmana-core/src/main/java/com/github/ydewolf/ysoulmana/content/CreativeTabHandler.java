package com.github.ydewolf.ysoulmana.content;

import com.github.ydewolf.ysoulmana.EBWManaMod;
import com.github.ydewolf.ysoulmana.content.item.ModItems;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabHandler {
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeTabs.ACCESSORIES.getKey()) {
            event.accept(ModItems.MANA_REGEN_RING.get());
            event.accept(ModItems.CASTER_RING.get());
        }
    }
}