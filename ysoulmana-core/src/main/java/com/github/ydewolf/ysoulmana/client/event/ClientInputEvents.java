package com.github.ydewolf.ysoulmana.client.event;

import com.github.ydewolf.ysoulmana.SoulManaMod;
import com.github.ydewolf.ysoulmana.client.keybind.KeyMappings;
import com.github.ydewolf.ysoulmana.content.item.CasterRingItem;
import com.github.ydewolf.ysoulmana.network.CastRingSpellC2SPacket;
import com.github.ydewolf.ysoulmana.network.ModMessages;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoulManaMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientInputEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (KeyMappings.CAST_RING_SPELL_KEY.consumeClick()) {
            CasterRingItem ringItem = CasterRingItem.getEquipped(mc.player);
            if (ringItem != null) {
                ModMessages.sendToServer(new CastRingSpellC2SPacket());
            }
        }
    }
}