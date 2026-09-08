package com.github.ydewolf.ebwplayermana.attribute;

import com.github.ydewolf.ebwplayermana.EBWManaMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, EBWManaMod.MODID);

    public static final RegistryObject<Attribute> MAX_MANA = ATTRIBUTES.register("max_mana",
            () -> new RangedAttribute("attribute.name.ebwplayermana.max_mana", 50.0, 0.0D, 2048.0D).setSyncable(true));

    public static final RegistryObject<Attribute> MANA_REGEN = ATTRIBUTES.register("mana_regen",
            () -> new RangedAttribute("attribute.name.ebwplayermana.mana_regen", 1.0D, 0.0D, 100.0D).setSyncable(true));

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
