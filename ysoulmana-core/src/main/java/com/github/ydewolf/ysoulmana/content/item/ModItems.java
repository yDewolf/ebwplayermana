package com.github.ydewolf.ysoulmana.content.item;

import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.content.spell_tier.NoviceTier;
import com.binaris.wizardry.setup.registries.Elements;
import com.github.ydewolf.ysoulmana.EBWManaMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EBWManaMod.MODID);

    public static final RegistryObject<Item> MANA_REGEN_RING = ITEMS.register("mana_regen_ring",
            () -> new ManaRegenRingItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CASTER_RING = ITEMS.register("caster_ring",
            () -> new CasterRingItem(new NoviceTier(), Elements.MAGIC));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}