package com.github.ydewolf.ysoulmana.client.keybind;

import com.github.ydewolf.ysoulmana.EBWManaMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = EBWManaMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyMappings {

    public static final KeyMapping CAST_RING_SPELL_KEY = new KeyMapping(
            "key.ebwplayermana.cast_ring_spell",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.ebwplayermana"
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
//        TODO
//        event.register(CAST_RING_SPELL_KEY);
    }
}