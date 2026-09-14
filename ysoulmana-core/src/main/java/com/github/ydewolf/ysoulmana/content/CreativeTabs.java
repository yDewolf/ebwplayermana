package com.github.ydewolf.ysoulmana.content;

import com.github.ydewolf.ysoulmana.SoulManaMod;
import com.github.ydewolf.ysoulmana.content.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SoulManaMod.MODID);

    public static final RegistryObject<CreativeModeTab> ACCESSORIES = CREATIVE_MODE_TABS.register("accessories",
        () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.MANA_REGEN_RING.get()))
                .title(Component.translatable("ysoulmana.creative_tab.accessories"))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.MANA_REGEN_RING.get());
                    output.accept(ModItems.CASTER_RING.get());
                })
                .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
