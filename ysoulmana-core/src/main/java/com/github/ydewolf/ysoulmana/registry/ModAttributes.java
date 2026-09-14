package com.github.ydewolf.ysoulmana.registry;

import com.github.ydewolf.ysoulmana.SoulManaMod;
import com.github.ydewolf.ysoulmana.config.SoulManaConfig;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = SoulManaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SoulManaMod.MODID);

    public static final RegistryObject<Attribute> MAX_MANA = registerAttribute("max_mana",
            () -> new RangedAttribute("attribute.name.ebwplayermana.max_mana", 50.0D, 0.0D, Integer.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> MANA_REGEN = registerAttribute("mana_regen",
            () -> new RangedAttribute("attribute.name.ebwplayermana.mana_regen", 0.5D, 0.0D, 100.0D).setSyncable(true));

    public static <T extends Attribute> RegistryObject<T> registerAttribute(String name, Supplier<T> attributeSupplier) {
        return ATTRIBUTES.register(name, attributeSupplier);
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
