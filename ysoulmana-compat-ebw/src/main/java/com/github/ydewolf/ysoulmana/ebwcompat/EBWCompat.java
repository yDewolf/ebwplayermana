package com.github.ydewolf.ysoulmana.ebwcompat;

import com.binaris.wizardry.core.event.WizardryEventBus;
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

@Mod(EBWCompat.MODID)
public class EBWCompat {
    public static final String MODID = "ysoulmana-ebwcompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EBWCompat(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

//        ManaAttributes.register(modEventBus);
//
//        CreativeTabs.register(modEventBus);
//        ModItems.register(modEventBus);
//
//        WizardryEventBus wiz_bus = WizardryEventBus.getInstance();
//        EBWSpellEvents.register(wiz_bus);

//        context.registerConfig(ModConfig.Type.COMMON, EBWCompatConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
//        ModMessages.register();
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

}
