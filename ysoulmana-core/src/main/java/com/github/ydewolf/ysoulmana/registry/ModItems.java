package com.github.ydewolf.ysoulmana.registry;

import com.github.ydewolf.ysoulmana.SoulManaMod;
import com.github.ydewolf.ysoulmana.content.item.CasterRingItem;
import com.github.ydewolf.ysoulmana.content.item.ManaRegenRingItem;
import com.github.ydewolf.ysoulmana.content.item.debug.DebugManaStaffItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SoulManaMod.MODID);

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }

    public static final RegistryObject<Item> DEBUG_MANA_STAFF = registerItem("debug_mana_staff",
            () -> new DebugManaStaffItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MANA_REGEN_RING = registerItem("mana_regen_ring",
            () -> new ManaRegenRingItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CASTER_RING = registerItem("caster_ring",
            () -> new CasterRingItem(new Item.Properties().stacksTo(1)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}