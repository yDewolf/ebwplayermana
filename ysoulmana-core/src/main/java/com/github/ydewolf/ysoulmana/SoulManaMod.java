package com.github.ydewolf.ysoulmana;

import com.github.ydewolf.ysoulmana.config.SoulManaConfig;
import com.github.ydewolf.ysoulmana.registry.ModAttributes;
import com.github.ydewolf.ysoulmana.registry.ModItems;
import com.github.ydewolf.ysoulmana.registry.ModMessages;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SoulManaMod.MODID)
public class SoulManaMod {
    public static final String MODID = "ysoulmana"; // No spaces, no uppercase
    public static final Logger LOGGER = LogUtils.getLogger();

    public SoulManaMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        ModAttributes.register(modEventBus);
        ModItems.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, SoulManaConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}