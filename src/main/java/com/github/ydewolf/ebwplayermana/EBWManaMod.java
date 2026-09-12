package com.github.ydewolf.ebwplayermana;

import com.binaris.wizardry.core.event.WizardryEventBus;
import com.github.ydewolf.ebwplayermana.content.CreativeTabs;
import com.github.ydewolf.ebwplayermana.content.attribute.ManaAttributes;
import com.github.ydewolf.ebwplayermana.content.item.ModItems;
import com.github.ydewolf.ebwplayermana.events.EBWSpellEvents;
import com.github.ydewolf.ebwplayermana.network.ModMessages;
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
@Mod(EBWManaMod.MODID)
public class EBWManaMod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "ebwplayermana"; // No spaces, no uppercase
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public EBWManaMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        ManaAttributes.register(modEventBus);

        CreativeTabs.register(modEventBus);
        ModItems.register(modEventBus);

        WizardryEventBus wiz_bus = WizardryEventBus.getInstance();
        EBWSpellEvents.register(wiz_bus);

        context.registerConfig(ModConfig.Type.COMMON, PlayerManaConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}